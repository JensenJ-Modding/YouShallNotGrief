package net.youshallnotgrief.database.tables;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.EntityItemTransactionData;
import net.youshallnotgrief.database.data.SourceData;
import net.youshallnotgrief.database.manager.DataManager;
import net.youshallnotgrief.database.manager.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityItemTransactionDataManager extends DataManager<EntityItemTransactionData> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS entityItemTransactions " +
                "(id INTEGER PRIMARY KEY, timestamp DATETIME NOT NULL, lastTimestamp DATETIME NOT NULL, count INTEGER NOT NULL DEFAULT 1, entityID INTEGER NOT NULL, itemID INTEGER NOT NULL, amount INTEGER NOT NULL, sourceID INTEGER NOT NULL, " +
                "FOREIGN KEY (entityID) REFERENCES entities(entityID)," +
                "FOREIGN KEY (itemID) REFERENCES items(itemID)," +
                "FOREIGN KEY (sourceID) REFERENCES sources(sourceID));";
    }

    @Override
    protected String getRetrieveSQL() {
        return "SELECT timestamp, count, entities.entity, items.item, amount, sources.source, sources.sourceDesc FROM entityItemTransactions";
    }


    @Override
    protected String getCountSQL() {
        return "SELECT COUNT(*) FROM entityItemTransactions";
    }

    @Override
    protected String getCreateIndexSQL() {
        return "CREATE INDEX IF NOT EXISTS idx_entityItemTransactions_entityID ON entityItemTransactions (entityID);";
    }

    protected String getInsertSQL(){
        return "INSERT INTO entityItemTransactions (timestamp, lastTimestamp, count, entityID, itemID, amount, sourceID) " +
                "VALUES (?, ?, 1, ?, ?, ?, ?);";
    }

    protected void setInsertPreparedStatementValues(PreparedStatement preparedStatement, EntityItemTransactionData data) throws SQLException {
        int entityID = DatabaseManager.ENTITY_TABLE_MANAGER.getForeignKeyInDatabase(data.entityUUID);
        int itemID = DatabaseManager.ITEM_TABLE_MANAGER.getForeignKeyInDatabase(data.item);
        int sourceID = DatabaseManager.SOURCE_TABLE_MANAGER.getForeignKeyInDatabase(data.source);

        preparedStatement.setTimestamp(1, data.timestamp);
        preparedStatement.setTimestamp(2, data.timestamp);
        preparedStatement.setInt(3, entityID);
        preparedStatement.setInt(4, itemID);
        preparedStatement.setInt(5, data.amount);
        preparedStatement.setInt(6, sourceID);
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE entityItemTransactions SET count = count + 1, lastTimestamp = ? " +
                "WHERE entityID = ? AND itemID = ? AND amount = ? AND sourceID = ? AND (? - lastTimestamp) <= ?;";
    }

    @Override
    protected void setUpdatePreparedStatementValues(PreparedStatement preparedStatement, EntityItemTransactionData data) throws SQLException {
        int entityID = DatabaseManager.ENTITY_TABLE_MANAGER.getForeignKeyInDatabase(data.entityUUID);
        int itemID = DatabaseManager.ITEM_TABLE_MANAGER.getForeignKeyInDatabase(data.item);
        int sourceID = DatabaseManager.SOURCE_TABLE_MANAGER.getForeignKeyInDatabase(data.source);

        preparedStatement.setTimestamp(1, data.timestamp);
        preparedStatement.setInt(2, entityID);
        preparedStatement.setInt(3, itemID);
        preparedStatement.setInt(4, data.amount);
        preparedStatement.setInt(5, sourceID);
        preparedStatement.setTimestamp(6, data.timestamp);
        preparedStatement.setInt(7, ServerConfig.databaseMergeTime.get() * 1000);
    }

    @Override
    protected void appendFiltersToSQL(EntityItemTransactionData data, StringBuilder query) {
        if(data.entityUUID != null) query.append(" AND entities.entity = ?");
        if(data.item != null) query.append(" AND items.item = ?");
        //Amount is not included as we never filter by amount
        if(data.source != null) query.append(" AND sources.source = ? AND sources.sourceDesc = ?");
        query.append(" ORDER BY timestamp");
    }

    @Override
    public void appendJoinsToSQL(StringBuilder query) {
        query.append(
                " JOIN entities ON entityItemTransactions.entityID = entities.entityID" +
                " JOIN items ON entityItemTransactions.itemID = items.itemID" +
                " JOIN sources ON entityItemTransactions.sourceID = sources.sourceID"
        );
    }

    @Override
    protected int setRetrievePreparedStatementValues(PreparedStatement preparedStatement, EntityItemTransactionData data) throws SQLException {
        int index = 1;
        if(data.entityUUID != null) preparedStatement.setString(index++, data.entityUUID);
        if(data.item != null) preparedStatement.setString(index++, data.item);
        //Amount is not included as we never filter by amount
        if(data.source != null){
            preparedStatement.setString(index++, data.source.source());
            preparedStatement.setString(index++, data.source.sourceDesc());
        }
        return index;
    }

    @Override
    public EntityItemTransactionData mapDataFromResultSet(ResultSet set) throws SQLException {
        return new EntityItemTransactionData(
                set.getTimestamp("timestamp"),
                set.getInt("count"),
                set.getString("entity"),
                set.getString("item"),
                set.getInt("amount"),
                new SourceData(set.getString("source"), set.getString("sourceDesc"))
        );
    }
}
