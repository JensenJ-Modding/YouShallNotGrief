package net.youshallnotgrief.database.manager;

import java.io.File;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import dev.architectury.event.events.common.LifecycleEvent;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

public class DatabaseLifecycleManager {

    private static Connection cachedDatabaseConnection = null;
    private static MinecraftServer minecraftServer = null;

    private static final AtomicInteger threadNumber = new AtomicInteger(1);
    public static ExecutorService executorService = null;

    public static void registerLifecycleEvents() {
        LifecycleEvent.SERVER_BEFORE_START.register((MinecraftServer server) -> {
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
            minecraftServer = null;
            int threadTimeout = ServerConfig.databaseExitTime.get();
            executorService.shutdown();
            YouShallNotGriefMod.LOGGER.info(
                    "Shutting down database threads. Waiting {} seconds before closing forcefully.", threadTimeout);
            try {
                if (executorService.awaitTermination(threadTimeout, TimeUnit.SECONDS)) {
                    YouShallNotGriefMod.LOGGER.info("Threads shut down gracefully.");
                } else {
                    executorService.shutdownNow();
                    YouShallNotGriefMod.LOGGER.error("Threads were terminated as timeout expired.");
                }
            } catch (InterruptedException e) {
                YouShallNotGriefMod.LOGGER.error("Thread shutdown was interrupted.");
            }

            DatabaseManager.clearCaches();
            if (cachedDatabaseConnection == null) {
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

        // If we already have a connection use that
        if (cachedDatabaseConnection != null) {
            try {
                if (!cachedDatabaseConnection.isValid(3)) {
                    YouShallNotGriefMod.LOGGER.error("Database connection timed out. Attempting reconnection.");
                } else {
                    return cachedDatabaseConnection;
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error(e.toString());
            }
        }

        Connection connection = null;
        if (minecraftServer == null) {
            YouShallNotGriefMod.LOGGER.error("Tried to establish database connection when server was null.");
            return null;
        }
        try {
            File folder = new File(getDatabaseWorldPath() + "temp");
            if (folder.exists() || folder.mkdirs()) {
                String databasePath = "jdbc:sqlite:" + getDatabaseWorldPath() + YouShallNotGriefMod.MOD_ID + ".db";
                connection = DriverManager.getConnection(databasePath);
                connection.setAutoCommit(true);
            } else {
                YouShallNotGriefMod.LOGGER.error("Failed to create folder for database.");
            }
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Failed to connect to database: ");
            throw new RuntimeException(e.toString());
        }
        YouShallNotGriefMod.LOGGER.info("Established database connection.");

        cachedDatabaseConnection = connection;

        // First time connection setup
        if (connection != null) {
            if (isFirstConnection) {
                YouShallNotGriefMod.LOGGER.info("Performing startup. This may take a while.");
                DatabaseManager.startupQueries();
                YouShallNotGriefMod.LOGGER.info("Performed startup queries.");
            }
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error(e.toString());
            }
        }
        DatabaseManager.clearCaches();
        return connection;
    }

    public static String getDatabaseWorldPath() {
        return minecraftServer.getWorldPath(LevelResource.ROOT).toAbsolutePath() + "/youshallnotgrief/";
    }
}
