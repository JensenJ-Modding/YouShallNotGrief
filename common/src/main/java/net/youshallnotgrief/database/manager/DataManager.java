package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.data.BaseData;
import net.youshallnotgrief.inspection.RetrieveResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class DataManager<InsertData extends BaseData> extends TableManager<InsertData> {

    protected abstract String getRetrieveSQL();
    protected abstract int setRetrievePreparedStatementValues(PreparedStatement preparedStatement, InsertData data) throws SQLException;
    protected abstract InsertData mapDataFromResultSet(ResultSet set) throws SQLException;
    protected abstract void appendJoinsToSQL(StringBuilder builder);
    protected abstract void appendFiltersToSQL(InsertData data, StringBuilder builder);

    protected abstract String getCountSQL();

    //The query and count logic is the same, except that count has two parameters chucked on the end
    protected void setRetrievePreparedStatementValuesWithLimits(PreparedStatement preparedStatement, InsertData data, int limit, int offset) throws SQLException {
        int index = setRetrievePreparedStatementValues(preparedStatement, data);
        preparedStatement.setInt(index++, limit);
        preparedStatement.setInt(index, offset);
    }

    protected String getRetrieveSQLInternal(InsertData data){
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

    public void retrieveFromDatabase(InsertData data, int limit, int offset, Consumer<RetrieveResult<InsertData>> callback) {
        if(DatabaseLifecycleManager.executorService == null){
            return;
        }

        Callable<RetrieveResult<InsertData>> task = () -> {
            ArrayList<InsertData> dataToReturn = new ArrayList<>();
            Connection database = DatabaseLifecycleManager.getDatabaseConnection();
            if (database == null) {
                return null;
            }

            int count = 0;
            String countQuery = getCountSQLInternal(data);
            try (PreparedStatement preparedStatement = database.prepareStatement(countQuery)) {
                setRetrievePreparedStatementValues(preparedStatement, data);
                ResultSet set = preparedStatement.executeQuery();
                if (set.next()) {
                    count = set.getInt(1);
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Error retrieving count from database:");
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(countQuery);
                return null;
            }

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
}
