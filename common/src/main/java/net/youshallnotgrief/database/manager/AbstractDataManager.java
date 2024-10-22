package net.youshallnotgrief.database.manager;

import net.minecraft.client.Minecraft;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.database.manager.block.TableManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractDataManager<InsertData, QueryData> implements DataManager<InsertData, QueryData>, TableManager<InsertData> {
    protected Set<InsertData> QUEUED_DATA = ConcurrentHashMap.newKeySet();
    protected ArrayList<TableManager<InsertData>> TABLE_MANAGERS = new ArrayList<>();
    private static final int MAX_QUEUE_SIZE = 1000;
    private static final AtomicBoolean isCommitting = new AtomicBoolean(false);

    @Override
    public void addToDatabase(InsertData data){
        if(Minecraft.getInstance().isSameThread()){
            YouShallNotGriefMod.LOGGER.error("Error adding entry to database. addToDatabase executed on client side.");
        }
        QUEUED_DATA.add(data);
        if(QUEUED_DATA.size() >= MAX_QUEUE_SIZE && isCommitting.compareAndSet(false, true)) {
            commitQueuedToDatabase();
        }
    }

    @Override
    public ArrayList<TableManager<InsertData>> getForeignTables() {
        return TABLE_MANAGERS;
    }

    @Override
    public void commitQueuedToDatabase(){
        try {
            DatabaseManager.executorService.submit(() -> {
                if (QUEUED_DATA.isEmpty()) {
                    return;
                }

                Connection database = DatabaseManager.getDatabaseConnection();
                if (database == null) {
                    return;
                }

                for (TableManager<InsertData> tableManager : TABLE_MANAGERS) {
                    commitTable(database, tableManager);
                }
                commitTable(database, this);
                QUEUED_DATA.clear();
                isCommitting.set(false);
            });
        } catch (RejectedExecutionException e){
            YouShallNotGriefMod.LOGGER.error("Failed to commit queued data to database. Task could not be scheduled.");
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }
    }

    private void commitTable(Connection database, TableManager<InsertData> tableManager){
        try (PreparedStatement preparedStatement = database.prepareStatement(tableManager.getInsertSQL())) {
            for (InsertData data : QUEUED_DATA) {
                tableManager.setInsertPreparedStatementValues(preparedStatement, data);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            database.commit();
            tableManager.onInsertionCompleted();
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error inserting data into database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(tableManager.getInsertSQL());
            try {
                database.rollback();
            } catch (SQLException ex) {
                YouShallNotGriefMod.LOGGER.error("Error performing rollback of database:");
                YouShallNotGriefMod.LOGGER.error(ex.toString());
            }
        }
    }

    @Override
    public Future<ArrayList<InsertData>> retrieveFromDatabase(QueryData data) {
        return DatabaseManager.executorService.submit(() -> {
            ArrayList<InsertData> dataToReturn = new ArrayList<>();
            Connection database = DatabaseManager.getDatabaseConnection();
            if (database == null) {
                return null;
            }

            try (PreparedStatement preparedStatement = database.prepareStatement(getQuerySQL())) {
                setQueryPreparedStatementValues(preparedStatement, data);
                ResultSet set = preparedStatement.executeQuery();
                while (set.next()) {
                    try {
                        dataToReturn.add(mapDataFromResultSet(set));
                    } catch (SQLException e) {
                        YouShallNotGriefMod.LOGGER.error("Error retrieving data from database when performing mapping data from result set.");
                        YouShallNotGriefMod.LOGGER.error(e.toString());
                        YouShallNotGriefMod.LOGGER.error(getQuerySQL());
                        return null;
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Error retrieving data from database:");
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(getQuerySQL());
                return null;
            }

            return dataToReturn;
        });
    }
}
