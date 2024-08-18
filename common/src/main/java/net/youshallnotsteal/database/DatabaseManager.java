package net.youshallnotsteal.database;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotsteal.YouShallNotStealMod;

import java.sql.*;

public class DatabaseManager {

    public static Connection databaseConnection = null;

    public static void commitAllQueuedDataToDatabase(){
        BlockSetManager.commitQueuedToDatabase();
    }

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

    public static boolean initialiseDatabase(){
        if(databaseConnection == null) {
            return false;
        }

        return DatabaseUtils.createTable("CREATE TABLE IF NOT EXISTS blockSets (id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER, z INTEGER, timestamp DATETIME NOT NULL, blockName TEXT NOT NULL, dimension TEXT NOT NULL, cause TEXT NOT NULL, causeDesc TEXT NOT NULL);");
    }
}
