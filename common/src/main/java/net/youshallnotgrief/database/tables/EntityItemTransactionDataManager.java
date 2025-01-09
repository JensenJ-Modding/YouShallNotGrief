package net.youshallnotgrief.database.tables;

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
                "(id INTEGER PRIMARY KEY, timestamp DATETIME NOT NULL, entityID INTEGER NOT NULL, itemID INTEGER NOT NULL, amount INTEGER NOT NULL, sourceID INTEGER NOT NULL, " +
                "FOREIGN KEY (entityID) REFERENCES entities(entityID)," +
                "FOREIGN KEY (itemID) REFERENCES items(itemID)," +
                "FOREIGN KEY (sourceID) REFERENCES sources(sourceID));";
    }

    @Override
    protected String getRetrieveSQL() {
        return "SELECT timestamp, entities.entity, items.item, amount, sources.source, sources.sourceDesc FROM entityItemTransactions";
    }

    protected String getInsertSQL(){
        return "INSERT INTO entityItemTransactions (timestamp, entityID, itemID, amount, sourceID) " +
                "VALUES (?, ?, ?, ?, ?);";
    }

    @Override
    protected String getCountSQL() {
        return "SELECT COUNT(*) FROM entityItemTransactions";
    }

    protected void setInsertPreparedStatementValues(PreparedStatement preparedStatement, EntityItemTransactionData data) throws SQLException {
        int entityID = DatabaseManager.ENTITY_TABLE_MANAGER.getForeignKeyInDatabase(data.entityUUID);
        int itemID = DatabaseManager.ITEM_TABLE_MANAGER.getForeignKeyInDatabase(data.item);
        int sourceID = DatabaseManager.SOURCE_TABLE_MANAGER.getForeignKeyInDatabase(data.source);

        preparedStatement.setTimestamp(1, data.timestamp);
        preparedStatement.setInt(2, entityID);
        preparedStatement.setInt(3, itemID);
        preparedStatement.setInt(4, data.amount);
        preparedStatement.setInt(5, sourceID);
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
    protected void appendJoinsToSQL(StringBuilder query) {
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
    protected EntityItemTransactionData mapDataFromResultSet(ResultSet set) throws SQLException {
        return new EntityItemTransactionData(
                set.getTimestamp("timestamp"),
                set.getString("entity"),
                set.getString("item"),
                set.getInt("amount"),
                new SourceData(set.getString("source"), set.getString("sourceDesc"))
        );
    }
}
