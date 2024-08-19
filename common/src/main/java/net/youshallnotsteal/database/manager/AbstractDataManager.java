package net.youshallnotsteal.database.manager;

import net.youshallnotsteal.YouShallNotStealMod;
import net.youshallnotsteal.database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public abstract class AbstractDataManager<T> implements DataManager<T> {
    protected HashSet<T> queuedData = new HashSet<>();
    private static final int MAX_QUEUE_SIZE = 10000;

    @Override
    public void addToDatabase(T data){
        if(DatabaseManager.databaseConnection == null){
            return;
        }

        queuedData.add(data);
        if(queuedData.size() > MAX_QUEUE_SIZE){
            commitQueuedToDatabase();
        }
    }

    @Override
    public void commitQueuedToDatabase(){
        if(DatabaseManager.databaseConnection == null){
            return;
        }

        try (PreparedStatement preparedStatement = DatabaseManager.databaseConnection.prepareStatement(getInsertSQL())) {
            for (T data : queuedData) {
                setPreparedStatementValues(preparedStatement, data);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            DatabaseManager.databaseConnection.commit();
            queuedData.clear();
        } catch (SQLException e) {
            YouShallNotStealMod.LOGGER.error(e.toString());
            try {
                DatabaseManager.databaseConnection.rollback();
            } catch (SQLException ex) {
                YouShallNotStealMod.LOGGER.error(ex.toString());
            }
        }
    }

    @Override
    public void createTableIfNotExists(){
        try (PreparedStatement preparedStatement = DatabaseManager.databaseConnection.prepareStatement(getCreateTableSQL())){
            preparedStatement.execute();
            DatabaseManager.databaseConnection.commit();
        }catch(SQLException e){
            YouShallNotStealMod.LOGGER.error(e.toString());
            try {
                DatabaseManager.databaseConnection.rollback();
            } catch (SQLException ex) {
                YouShallNotStealMod.LOGGER.error(ex.toString());
            }
        }
    }

    protected abstract String getInsertSQL();
    protected abstract String getCreateTableSQL();
    protected abstract void setPreparedStatementValues(PreparedStatement preparedStatement, T data) throws SQLException;
}
