package net.youshallnotgrief.database.manager;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseLifecycleManager {

    private static Connection cachedDatabaseConnection = null;
    private static MinecraftServer minecraftServer = null;

    private static final AtomicInteger threadNumber = new AtomicInteger(1);
    public static ExecutorService executorService = null;

    public static void registerLifecycleEvents(){
        LifecycleEvent.SERVER_STARTED.register((MinecraftServer server) -> {
            minecraftServer = server;
            threadNumber.set(1);
            executorService = Executors.newFixedThreadPool(ServerConfig.databaseThreadCount.get(), runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("YouShallNotGrief-" + threadNumber.getAndIncrement());
                return thread;
            });
            DatabaseManager.MAX_QUEUE_SIZE = ServerConfig.databaseQueueSize.get();
            cachedDatabaseConnection = getDatabaseConnection();
            DatabaseManager.clearCaches();
        });

        LifecycleEvent.SERVER_STOPPED.register((MinecraftServer server) -> {
            DatabaseManager.commitQueuedToDatabase();
            DatabaseManager.clearCaches();
            minecraftServer = null;
            int threadTimeout = 10;
            executorService.shutdown();
            YouShallNotGriefMod.LOGGER.info("Shutting down database threads. Waiting {} seconds before closing forcefully.", threadTimeout);
            try {
                if(executorService.awaitTermination(threadTimeout, TimeUnit.SECONDS)){
                    YouShallNotGriefMod.LOGGER.info("Threads shut down gracefully.");
                }else{
                    executorService.shutdownNow();
                    YouShallNotGriefMod.LOGGER.error("Threads were terminated as timeout expired.");
                }
            } catch (InterruptedException e) {
                YouShallNotGriefMod.LOGGER.error("Thread shutdown was interrupted.");
            }

            if(cachedDatabaseConnection == null){
                return;
            }

            try {
                cachedDatabaseConnection.close();
                cachedDatabaseConnection = null;
                YouShallNotGriefMod.LOGGER.info("Closed database connection.");
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error(e.toString());
            }
        });
    }

    public static Connection getDatabaseConnection() {
        boolean isFirstConnection = cachedDatabaseConnection == null;

        //If we already have a connection use that
        if(cachedDatabaseConnection != null) {
            try {
                if (!cachedDatabaseConnection.isValid(3)) {
                    YouShallNotGriefMod.LOGGER.error("Database connection timed out. Attempting reconnection.");
                }else{
                    return cachedDatabaseConnection;
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error(e.toString());
            }
        }

        Connection connection = null;
        if(minecraftServer == null) {
            YouShallNotGriefMod.LOGGER.error("Tried to establish database connection when server was null.");
            return null;
        }
        String DatabaseWorldPath = minecraftServer.getWorldPath(LevelResource.ROOT).toAbsolutePath() + "/";
        try {
            String databasePath = "jdbc:sqlite:" + DatabaseWorldPath + YouShallNotGriefMod.MOD_ID + ".db";
            connection = DriverManager.getConnection(databasePath);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Failed to connect to database: ");
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }
        YouShallNotGriefMod.LOGGER.info("Established database connection.");

        cachedDatabaseConnection = connection;

        //First time connection setup
        if(isFirstConnection && connection != null) {
            DatabaseManager.createTables();
        }
        DatabaseManager.clearCaches();
        return connection;
    }
}
