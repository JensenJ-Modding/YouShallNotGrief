package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.inspection.RetrieveResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class DataManager<InsertData> extends TableManager<InsertData> {

    protected abstract String getRetrieveSQL();
    protected abstract int setRetrievePreparedStatementValues(PreparedStatement preparedStatement, InsertData data) throws SQLException;
    protected abstract void appendFiltersToSQL(InsertData data, StringBuilder builder);
    public abstract void appendJoinsToSQL(StringBuilder builder);
    public abstract InsertData mapDataFromResultSet(ResultSet set) throws SQLException;

    protected abstract String getUpdateSQL();
    protected abstract void setUpdatePreparedStatementValues(PreparedStatement preparedStatement, InsertData data) throws SQLException;

    protected abstract String getCountSQL();
    protected abstract String getCreateIndexSQL();

    //The query and count logic is the same, except that count has two parameters chucked on the end
    protected void setRetrievePreparedStatementValuesWithLimits(PreparedStatement preparedStatement, InsertData data, int limit, int offset) throws SQLException {
        int index = setRetrievePreparedStatementValues(preparedStatement, data);
        preparedStatement.setInt(index++, limit);
        preparedStatement.setInt(index, offset);
    }

    private String getRetrieveSQLInternal(InsertData data){
        StringBuilder query = new StringBuilder();
        query.append(getRetrieveSQL());

        appendJoinsToSQL(query);
        query.append(" WHERE 1=1");
        appendFiltersToSQL(data, query);
        query.append(" DESC LIMIT ? OFFSET ?;");

        return query.toString();
    }

    private String getCountSQLInternal(InsertData data){
        StringBuilder query = new StringBuilder();
        query.append(getCountSQL());

        appendJoinsToSQL(query);
        query.append(" WHERE 1=1");
        appendFiltersToSQL(data, query);

        return query.toString();
    }

    public int getCountFromDatabase(InsertData data, Connection database){
        String countQuery = getCountSQLInternal(data);
        try (PreparedStatement preparedStatement = database.prepareStatement(countQuery)) {
            setRetrievePreparedStatementValues(preparedStatement, data);
            ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
                return set.getInt(1);
            }
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error retrieving count from database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(countQuery);
        }
        return -1;
    }

    public ArrayList<InsertData> getDataFromDatabase(InsertData data, Connection database, int limit, int offset){
        ArrayList<InsertData> dataToReturn = new ArrayList<>();
        String retrieveQuery = getRetrieveSQLInternal(data);
        try (PreparedStatement preparedStatement = database.prepareStatement(retrieveQuery)) {
            setRetrievePreparedStatementValuesWithLimits(preparedStatement, data, limit, offset);
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                try {
                    dataToReturn.add(mapDataFromResultSet(set));
                } catch (SQLException e) {
                    YouShallNotGriefMod.LOGGER.error("Error retrieving data from database when performing mapping data from result set.");
                    YouShallNotGriefMod.LOGGER.error(e.toString());
                    YouShallNotGriefMod.LOGGER.error(retrieveQuery);
                    return null;
                }
            }
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error retrieving data from database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(retrieveQuery);
            return null;
        }
        return dataToReturn;
    }


    public void retrieveFromDatabase(InsertData data, int limit, int offset, Consumer<RetrieveResult<InsertData>> callback) {
        if(DatabaseLifecycleManager.executorService == null)
            return;

        Callable<RetrieveResult<InsertData>> task = () -> {
            Connection database = DatabaseLifecycleManager.getDatabaseConnection();
            if (database == null) {
                return null;
            }

            int count = getCountFromDatabase(data, database);
            if(count == -1){
                return null;
            }
            ArrayList<InsertData> dataToReturn = getDataFromDatabase(data, database, limit, offset);
            return new RetrieveResult<>(dataToReturn, count);
        };

        DatabaseLifecycleManager.executorService.submit(() -> {
           try {
               RetrieveResult<InsertData> result = task.call();
               callback.accept(result);
           } catch(Exception e) {
               YouShallNotGriefMod.LOGGER.error("Error getting data from database retrieval:");
               YouShallNotGriefMod.LOGGER.error(e.toString());
               callback.accept(null);
           }
        });
    }

    @Override
    public void commitTable(){
        Connection database = DatabaseLifecycleManager.getDatabaseConnection();
        if(database == null) {
            return;
        }

        for (InsertData data : COMMITTING_QUEUED_DATA){
            try (PreparedStatement updatePreparedStatement = database.prepareStatement(getUpdateSQL())) {
                setUpdatePreparedStatementValues(updatePreparedStatement, data);
                int updatedRows = updatePreparedStatement.executeUpdate();

                if (updatedRows == 0){
                    PreparedStatement insertPreparedStatement = database.prepareStatement(getInsertSQL());
                    setInsertPreparedStatementValues(insertPreparedStatement, data);
                    insertPreparedStatement.executeUpdate();
                }

                database.commit();
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Error inserting or updating data in database:");
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(getUpdateSQL());
                YouShallNotGriefMod.LOGGER.error(getInsertSQL());
                try {
                    database.rollback();
                } catch (SQLException ex) {
                    YouShallNotGriefMod.LOGGER.error("Error performing rollback of database:");
                    YouShallNotGriefMod.LOGGER.error(ex.toString());
                }
            }
        }
        COMMITTING_QUEUED_DATA.clear();
    }
}