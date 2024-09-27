package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class AbstractDataManager<T, K> implements DataManager<T, K> {
    protected HashSet<T> queuedData = new HashSet<>();
    private static final int MAX_QUEUE_SIZE = 10000;

    @Override
    public void addToDatabase(T data){
        if(DatabaseManager.getDatabaseConnection() == null){
            return;
        }

        queuedData.add(data);
        if(queuedData.size() > MAX_QUEUE_SIZE){
            commitQueuedToDatabase();
        }
    }

    @Override
    public void commitQueuedToDatabase(){
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            return;
        }

        try (PreparedStatement preparedStatement = database.prepareStatement(getInsertSQL())) {
            for (T data : queuedData) {
                setInsertPreparedStatementValues(preparedStatement, data);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            database.commit();
            queuedData.clear();
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error inserting data into database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
            try {
                database.rollback();
            } catch (SQLException ex) {
                YouShallNotGriefMod.LOGGER.error("Error performing rollback of database:");
                YouShallNotGriefMod.LOGGER.error(ex.toString());
            }
        }
    }

    @Override
    public ArrayList<T> retrieveFromDatabase(K data) {
        ArrayList<T> dataToReturn = new ArrayList<T>();
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            return null;
        }

        try(PreparedStatement preparedStatement = database.prepareStatement(getQuerySQL())){
            setQueryPreparedStatementValues(preparedStatement, data);
            try(ResultSet set = preparedStatement.executeQuery()){
                while(set.next()){
                    dataToReturn.add(mapDataFromResultSet(set));
                }
            }
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error retrieving data from database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }

        return dataToReturn;
    }
}
