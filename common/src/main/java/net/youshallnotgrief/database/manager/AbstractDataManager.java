package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.database.manager.block.TableManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class AbstractDataManager<InsertData, QueryData> implements DataManager<InsertData, QueryData>, TableManager<InsertData> {
    protected HashSet<InsertData> QUEUED_DATA = new HashSet<>();
    protected ArrayList<TableManager<InsertData>> TABLE_MANAGERS = new ArrayList<>();
    private static final int MAX_QUEUE_SIZE = 10000;

    @Override
    public void addToDatabase(InsertData data){
        if(DatabaseManager.getDatabaseConnection() == null){
            return;
        }

        QUEUED_DATA.add(data);
        if(QUEUED_DATA.size() > MAX_QUEUE_SIZE){
            commitQueuedToDatabase();
        }
    }

    @Override
    public ArrayList<TableManager<InsertData>> getForeignTables() {
        return TABLE_MANAGERS;
    }

    @Override
    public void commitQueuedToDatabase(){
        if(QUEUED_DATA.isEmpty()){
            return;
        }

        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            return;
        }

        for (TableManager<InsertData> tableManager : TABLE_MANAGERS) {
            commitTable(database, tableManager);
        }
        commitTable(database, this);
        QUEUED_DATA.clear();
    }

    private void commitTable(Connection database, TableManager<InsertData> tableManager){
        try (PreparedStatement preparedStatement = database.prepareStatement(tableManager.getInsertSQL())) {
            for (InsertData data : QUEUED_DATA) {
                tableManager.setInsertPreparedStatementValues(preparedStatement, data);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            database.commit();
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
    public ArrayList<InsertData> retrieveFromDatabase(QueryData data) {
        ArrayList<InsertData> dataToReturn = new ArrayList<>();
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
