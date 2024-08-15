package net.youshallnotsteal.database;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotsteal.YouShallNotStealMod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public static Connection databaseConnection = null;

    public static void registerLifecycleEvents(){
        LifecycleEvent.SERVER_STARTED.register((MinecraftServer server) -> {
            databaseConnection = getDatabaseConnection(server);
            if(databaseConnection != null){
                YouShallNotStealMod.LOGGER.info("Established Database Connection");
            }
        });

        LifecycleEvent.SERVER_STOPPED.register((MinecraftServer server) -> {
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
        } catch (SQLException e) {
            YouShallNotStealMod.LOGGER.error(e.toString());
        }
        return connection;
    }
}
