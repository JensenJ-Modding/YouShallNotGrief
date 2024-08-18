package net.youshallnotsteal.database;

import net.youshallnotsteal.YouShallNotStealMod;
import net.youshallnotsteal.data.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public class BlockSetManager {

    static HashSet<BlockSetData> queuedData = new HashSet<>();
    private static final int MAX_QUEUE_SIZE = 2000;

    public static void addToDatabase(BlockSetData data){
        if(DatabaseManager.databaseConnection == null){
            return;
        }

        queuedData.add(data);
        if(queuedData.size() > MAX_QUEUE_SIZE){
            commitQueuedToDatabase();
        }
    }

    public static void commitQueuedToDatabase(){
        String addBlockInteractionSQL = "INSERT INTO blockSets (x, y, z, timestamp, blockName, dimension, cause, causeDesc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = DatabaseManager.databaseConnection.prepareStatement(addBlockInteractionSQL)){

            for(BlockSetData data : queuedData){
                preparedStatement.setInt(1, data.pos().getX());
                preparedStatement.setInt(2, data.pos().getY());
                preparedStatement.setInt(3, data.pos().getZ());
                preparedStatement.setTimestamp(4, data.time());
                preparedStatement.setString(5, data.blockName());
                preparedStatement.setString(6, data.dimension());
                preparedStatement.setString(7, data.cause());
                preparedStatement.setString(8, data.causeDesc());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            DatabaseManager.databaseConnection.commit();
            queuedData.clear();
        }catch(SQLException e){
            YouShallNotStealMod.LOGGER.error(e.toString());
            try {
                DatabaseManager.databaseConnection.rollback();
            } catch (SQLException ex) {
                YouShallNotStealMod.LOGGER.error(ex.toString());
            }
        }
    }
}
