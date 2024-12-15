package net.youshallnotgrief.database.manager;

import net.minecraft.client.Minecraft;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.database.manager.block.TableManager;
import net.youshallnotgrief.util.InspectionMode;
import net.youshallnotgrief.util.RetrieveResult;

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
    protected ArrayList<InsertData> COMMITTING_QUEUED_DATA = new ArrayList<>();
    protected ArrayList<TableManager<InsertData>> TABLE_MANAGERS = new ArrayList<>();
    private static final AtomicBoolean isCommitting = new AtomicBoolean(false);

    @Override
    public void addToDatabase(InsertData data){
        if(isClientSide()){
            throw new IllegalStateException("Failed to add data to Database Queue. addToDatabase called from clientside." + data);
        }
        QUEUED_DATA.add(data);
        if((QUEUED_DATA.size() >= DatabaseManager.MAX_QUEUE_SIZE || !InspectionMode.INSPECTING_PLAYERS.isEmpty())) {
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
            if(isCommitting.compareAndSet(false, true)) {
                if (DatabaseManager.executorService == null) {
                    return;
                }
                COMMITTING_QUEUED_DATA = new ArrayList<>(QUEUED_DATA);
                QUEUED_DATA.clear();
                DatabaseManager.executorService.submit(() -> {
                    if (COMMITTING_QUEUED_DATA.isEmpty()) {
                        isCommitting.set(false);
                        return;
                    }

                    Connection database = DatabaseManager.getDatabaseConnection();
                    if (database == null) {
                        isCommitting.set(false);
                        return;
                    }

                    for (TableManager<InsertData> tableManager : TABLE_MANAGERS) {
                        commitTable(database, tableManager);
                    }
                    commitTable(database, this);
                    COMMITTING_QUEUED_DATA.clear();
                    isCommitting.set(false);
                });
            }
        } catch (RejectedExecutionException e){
            YouShallNotGriefMod.LOGGER.error("Failed to commit queued data to database. Task could not be scheduled.");
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }
    }

    private void commitTable(Connection database, TableManager<InsertData> tableManager){
        try (PreparedStatement preparedStatement = database.prepareStatement(tableManager.getInsertSQL())) {
            for (InsertData data : COMMITTING_QUEUED_DATA) {
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
    public Future<RetrieveResult<InsertData>> retrieveFromDatabase(QueryData data, int limit, int offset) {
        if(DatabaseManager.executorService == null){
            return null;
        }

        return DatabaseManager.executorService.submit(() -> {
            ArrayList<InsertData> dataToReturn = new ArrayList<>();
            Connection database = DatabaseManager.getDatabaseConnection();
            if (database == null) {
                return null;
            }

            int count = 0;
            try (PreparedStatement preparedStatement = database.prepareStatement(getCountSQL())) {
                setCountPreparedStatementValues(preparedStatement, data);
                ResultSet set = preparedStatement.executeQuery();
                if (set.next()) {
                    count = set.getInt(1);
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Error retrieving count from database:");
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(getCountSQL());
                return null;
            }

            try (PreparedStatement preparedStatement = database.prepareStatement(getQuerySQL())) {
                setQueryPreparedStatementValues(preparedStatement, data, limit, offset);
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

            return new RetrieveResult<>(dataToReturn, count);
        });
    }

    private static boolean isClientSide() {
        try {
            return Minecraft.getInstance().isSameThread();
        } catch (RuntimeException e) {
            return false;
        }
    }
}
