package net.youshallnotgrief.database.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.youshallnotgrief.YouShallNotGriefMod;

public abstract class TableManager<InsertData> {

    private final Set<InsertData> QUEUED_DATA = ConcurrentHashMap.newKeySet();
    protected ArrayList<InsertData> COMMITTING_QUEUED_DATA = new ArrayList<>();

    public void queueData(InsertData data) {
        QUEUED_DATA.add(data);
    }

    // This copies the contents of queued into a commiting array so we don't conflict with main-thread operations while
    // committing the table
    public void prepareToCommitTable() {
        COMMITTING_QUEUED_DATA = new ArrayList<>(QUEUED_DATA);
        QUEUED_DATA.clear();
    }

    // Called on database thread
    public void commitTable() {
        Connection database = DatabaseLifecycleManager.getDatabaseConnection();
        if (database == null) {
            return;
        }
        try (PreparedStatement preparedStatement = database.prepareStatement(getInsertSQL())) {
            for (InsertData data : COMMITTING_QUEUED_DATA) {
                setInsertPreparedStatementValues(preparedStatement, data);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            database.commit();
            onInsertionCompleted();

            COMMITTING_QUEUED_DATA.clear();
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error inserting data into database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(getInsertSQL());
            try {
                database.rollback();
            } catch (SQLException ex) {
                YouShallNotGriefMod.LOGGER.error("Error performing rollback of database:");
                YouShallNotGriefMod.LOGGER.error(ex.toString());
            }
        }
    }

    protected void onInsertionCompleted() {}

    public abstract String getCreateTableSQL();

    protected abstract String getInsertSQL();

    protected abstract void setInsertPreparedStatementValues(PreparedStatement preparedStatement, InsertData data)
            throws SQLException;
}
