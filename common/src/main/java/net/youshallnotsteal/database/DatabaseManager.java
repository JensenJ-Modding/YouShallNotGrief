package net.youshallnotsteal.database;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.youshallnotsteal.YouShallNotStealMod;
import net.youshallnotsteal.data.BlockInteractionData;

import java.sql.*;

public class DatabaseManager {

    public static Connection databaseConnection = null;

    public static void registerLifecycleEvents(){
        LifecycleEvent.SERVER_STARTED.register((MinecraftServer server) -> {
            databaseConnection = getDatabaseConnection(server);
            if(databaseConnection != null){
                YouShallNotStealMod.LOGGER.info("Established Database Connection");
            }
            if(!verifyDatabaseIntegrity()){
                YouShallNotStealMod.LOGGER.error("Failed to verify integrity of database.");
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
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            YouShallNotStealMod.LOGGER.error(e.toString());
        }
        return connection;
    }

    public static void addBlockInteractionToDatabase(BlockInteractionData data){
        String addBlockInteractionSQL = "INSERT INTO blockSets (x, y, z, timestamp, blockName, dimension, cause, causeDesc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(addBlockInteractionSQL)){
            preparedStatement.setInt(1, data.pos().getX());
            preparedStatement.setInt(2, data.pos().getY());
            preparedStatement.setInt(3, data.pos().getZ());
            preparedStatement.setTimestamp(4, data.time());
            preparedStatement.setString(5, data.blockName());
            preparedStatement.setString(6, data.dimension());
            preparedStatement.setString(7, data.cause());
            preparedStatement.setString(8, data.causeDesc());

            preparedStatement.execute();
            databaseConnection.commit();
        }catch(SQLException e){
            YouShallNotStealMod.LOGGER.error(e.toString());
            try {
                databaseConnection.rollback();
            } catch (SQLException ex) {
                YouShallNotStealMod.LOGGER.error(ex.toString());
            }
        }
    }

    public static boolean verifyDatabaseIntegrity(){
        if(databaseConnection == null) {
            return false;
        }

        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS blockSets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                x INTEGER, y INTEGER, z INTEGER,
                timestamp DATETIME NOT NULL,
                blockName TEXT NOT NULL,
                dimension TEXT NOT NULL,
                cause TEXT NOT NULL,
                causeDesc TEXT NOT NULL
                );
            """;

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(createTableSQL)){
            preparedStatement.execute();
            databaseConnection.commit();
        }catch(SQLException e){
            YouShallNotStealMod.LOGGER.error(e.toString());
            try {
                databaseConnection.rollback();
            } catch (SQLException ex) {
                YouShallNotStealMod.LOGGER.error(ex.toString());
            }
        }

        return true;
    }
}
