package net.youshallnotsteal.database;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotsteal.YouShallNotStealMod;
import net.youshallnotsteal.database.manager.BlockSetManager;
import net.youshallnotsteal.database.manager.DataManager;

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
                YouShallNotStealMod.LOGGER.info("Established Database Connection");
            }
            if(!initialiseDatabase()){
                YouShallNotStealMod.LOGGER.error("Failed to initialise database.");
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
                YouShallNotStealMod.LOGGER.info("Closing database connection.");
            } catch (SQLException e) {
                YouShallNotStealMod.LOGGER.error(e.toString());
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
            YouShallNotStealMod.LOGGER.error(e.toString());
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
