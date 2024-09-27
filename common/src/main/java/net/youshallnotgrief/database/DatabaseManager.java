package net.youshallnotgrief.database;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.manager.BlockSetManager;
import net.youshallnotgrief.database.manager.DataManager;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static Connection cachedDatabaseConnection = null;
    private static MinecraftServer minecraftServer = null;

    private static final ArrayList<DataManager<?, ?>> DATA_MANAGERS = new ArrayList<>();
    public static final BlockSetManager BLOCK_SET_MANAGER = registerDataManager(new BlockSetManager());

    public static void registerLifecycleEvents(){
        LifecycleEvent.SERVER_STARTED.register((MinecraftServer server) -> {
            minecraftServer = server;
            cachedDatabaseConnection = getDatabaseConnection();
        });

        LifecycleEvent.SERVER_STOPPED.register((MinecraftServer server) -> {
            commitAllQueuedDataToDatabase();
            minecraftServer = null;

            try {
                cachedDatabaseConnection.close();
                cachedDatabaseConnection = null;
                YouShallNotGriefMod.LOGGER.info("Closing database connection.");
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
            for (DataManager<?, ?> dataManager : DATA_MANAGERS) {
                createTableIfNotExists(connection, dataManager.getCreateTableSQL());
            }
        }

        cachedDatabaseConnection = connection;
        return connection;
    }

    private static void createTableIfNotExists(Connection database, String createTableSQL){
        try (PreparedStatement preparedStatement = database.prepareStatement(createTableSQL)){
            preparedStatement.execute();
            database.commit();
        }catch(SQLException e){
            YouShallNotGriefMod.LOGGER.error(e.toString());
            try {
                database.rollback();
            } catch (SQLException ex) {
                YouShallNotGriefMod.LOGGER.error(ex.toString());
            }
        }
    }


    private static <T extends DataManager<?, ?>> T registerDataManager(T dataManager){
        DATA_MANAGERS.add(dataManager);
        return dataManager;
    }

    public static void commitAllQueuedDataToDatabase(){
        for(DataManager<?, ?> dataManager : DATA_MANAGERS){
            dataManager.commitQueuedToDatabase();
        }
    }
}