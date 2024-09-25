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

    public static Connection databaseConnection = null;

    private static final ArrayList<DataManager<?>> DATA_MANAGERS = new ArrayList<>();
    public static final BlockSetManager BLOCK_SET_MANAGER = registerDataManager(new BlockSetManager());

    public static void registerLifecycleEvents(){
        LifecycleEvent.SERVER_STARTED.register((MinecraftServer server) -> {
            databaseConnection = getDatabaseConnection(server);
            if(databaseConnection != null){
                YouShallNotGriefMod.LOGGER.info("Established Database Connection");
            }
            if(!initialiseDatabase()){
                YouShallNotGriefMod.LOGGER.error("Failed to initialise database.");
            }
        });

        LifecycleEvent.SERVER_STOPPED.register((MinecraftServer server) -> {
            if(databaseConnection == null){
                return;
            }

            commitAllQueuedDataToDatabase();

            try {
                databaseConnection.close();
                databaseConnection = null;
                YouShallNotGriefMod.LOGGER.info("Closing database connection.");
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error(e.toString());
            }
        });
    }

    public static Connection getDatabaseConnection(MinecraftServer server){
        Connection connection = null;
        if(server == null) {
            return null;
        }
        String DatabaseWorldPath = server.getWorldPath(LevelResource.ROOT).toAbsolutePath() + "/";
        try {
            String databasePath = "jdbc:sqlite:" + DatabaseWorldPath + "youshallnotsteal.db";
            connection = DriverManager.getConnection(databasePath);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }
        return connection;
    }

    private static <T extends DataManager<?>> T registerDataManager(T dataManager){
        DATA_MANAGERS.add(dataManager);
        return dataManager;
    }

    public static void commitAllQueuedDataToDatabase(){
        for(DataManager<?> dataManager : DATA_MANAGERS){
            dataManager.commitQueuedToDatabase();
        }
    }

    public static boolean initialiseDatabase(){
        if(databaseConnection == null) {
            return false;
        }

        for(DataManager<?> dataManager : DATA_MANAGERS){
            dataManager.createTableIfNotExists();
        }
        return true;
    }
}
