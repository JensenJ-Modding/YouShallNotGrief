package net.youshallnotgrief.database;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.manager.AbstractDataManager;
import net.youshallnotgrief.database.manager.block.BlockSetDataManager;
import net.youshallnotgrief.database.manager.block.TableManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private static Connection cachedDatabaseConnection = null;
    private static MinecraftServer minecraftServer = null;

    private static final ArrayList<AbstractDataManager<?, ?>> DATA_MANAGERS = new ArrayList<>();
    public static final BlockSetDataManager BLOCK_SET_MANAGER = registerDataManager(new BlockSetDataManager());

    private static final int databaseThreadCount = 4;
    public static ExecutorService executorService = Executors.newFixedThreadPool(databaseThreadCount);

    public static void registerLifecycleEvents(){
        LifecycleEvent.SERVER_STARTED.register((MinecraftServer server) -> {
            minecraftServer = server;
            cachedDatabaseConnection = getDatabaseConnection();
            clearAllCaches();
            executorService = Executors.newFixedThreadPool(databaseThreadCount);
        });

        LifecycleEvent.SERVER_STOPPED.register((MinecraftServer server) -> {
            commitAllQueuedDataToDatabase();
            clearAllCaches();
            minecraftServer = null;
            int threadTimeout = 10;
            executorService.shutdown();
            YouShallNotGriefMod.LOGGER.info("Shutting down database threads. Waiting {} seconds before closing forcefully.", threadTimeout);
            try {
                if(executorService.awaitTermination(threadTimeout, TimeUnit.SECONDS)){
                    YouShallNotGriefMod.LOGGER.info("Threads shut down gracefully.");
                }else{
                    YouShallNotGriefMod.LOGGER.error("Threads were terminated as timeout expired.");
                }
            } catch (InterruptedException e) {
                YouShallNotGriefMod.LOGGER.error("Thread shutdown was interrupted.");
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
            String databasePath = "jdbc:sqlite:" + DatabaseWorldPath + "youshallnotsteal.db";
            connection = DriverManager.getConnection(databasePath);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Failed to connect to database: ");
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }
        YouShallNotGriefMod.LOGGER.info("Established database connection.");

        //First time connection setup
        if(isFirstConnection && connection != null) {
            for (AbstractDataManager<?, ?> dataManager : DATA_MANAGERS) {
                //Set up all foreign tables
                for(TableManager<?> table : dataManager.getForeignTables()){
                    createTableIfNotExists(connection, table.getCreateTableSQL());
                }
                createTableIfNotExists(connection, dataManager.getCreateTableSQL());
            }
        }

        clearAllCaches();
        cachedDatabaseConnection = connection;
        return connection;
    }

    private static void createTableIfNotExists(Connection database, String createTableSQL){
        try (PreparedStatement preparedStatement = database.prepareStatement(createTableSQL)){
            preparedStatement.execute();
            database.commit();
        }catch(SQLException e){
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(createTableSQL);
            try {
                database.rollback();
            } catch (SQLException ex) {
                YouShallNotGriefMod.LOGGER.error(ex.toString());
            }
        }
    }

    private static <DataManager extends AbstractDataManager<?, ?>> DataManager registerDataManager(DataManager dataManager){
        DATA_MANAGERS.add(dataManager);
        dataManager.registerForeignTables();
        return dataManager;
    }

    public static void commitAllQueuedDataToDatabase(){
        for(AbstractDataManager<?, ?> dataManager : DATA_MANAGERS){
            dataManager.commitQueuedToDatabase();
        }
    }

    public static void clearAllCaches(){
        for(AbstractDataManager<?, ?> dataManager : DATA_MANAGERS){
            for(TableManager<?> tableManager : dataManager.getForeignTables()){
                tableManager.clearCache();
            }
            dataManager.clearCache();
        }
    }
}
