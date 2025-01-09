package net.youshallnotgrief.database.tables;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.database.data.BlockItemTransactionData;
import net.youshallnotgrief.database.data.SourceData;
import net.youshallnotgrief.database.manager.DataManager;
import net.youshallnotgrief.database.manager.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockItemTransactionDataManager extends DataManager<BlockItemTransactionData> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockItemTransactions " +
                "(id INTEGER PRIMARY KEY, timestamp DATETIME NOT NULL, posID INTEGER NOT NULL, dimID INTEGER NOT NULL, itemID INTEGER NOT NULL, amount INTEGER NOT NULL, sourceID INTEGER NOT NULL, " +
                "FOREIGN KEY (posID) REFERENCES positions(posID)," +
                "FOREIGN KEY (dimID) REFERENCES dimensions(dimID)," +
                "FOREIGN KEY (itemID) REFERENCES items(itemID)," +
                "FOREIGN KEY (sourceID) REFERENCES sources(sourceID));";
    }

    @Override
    protected String getRetrieveSQL() {
        return "SELECT timestamp, positions.x, positions.y, positions.z, dimensions.dimension, " +
                "items.item, amount, sources.source, sources.sourceDesc FROM blockItemTransactions";
    }

    protected String getInsertSQL(){
        return "INSERT INTO blockItemTransactions (timestamp, posID, dimID, itemID, amount, sourceID) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected String getCountSQL() {
        return "SELECT COUNT(*) FROM blockItemTransactions";
    }

    protected void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockItemTransactionData data) throws SQLException {
        int posID = DatabaseManager.POSITION_TABLE_MANAGER.getForeignKeyInDatabase(data.position);
        int dimID = DatabaseManager.DIMENSION_TABLE_MANAGER.getForeignKeyInDatabase(data.dimension);
        int itemID = DatabaseManager.ITEM_TABLE_MANAGER.getForeignKeyInDatabase(data.item);
        int sourceID = DatabaseManager.SOURCE_TABLE_MANAGER.getForeignKeyInDatabase(data.source);

        preparedStatement.setTimestamp(1, data.timestamp);
        preparedStatement.setInt(2, posID);
        preparedStatement.setInt(3, dimID);
        preparedStatement.setInt(4, itemID);
        preparedStatement.setInt(5, data.amount);
        preparedStatement.setInt(6, sourceID);
    }

    @Override
    protected void appendFiltersToSQL(BlockItemTransactionData data, StringBuilder query) {
        if(data.position != null) query.append(" AND positions.x = ? AND positions.y = ? AND positions.z = ?");
        if(data.dimension != null) query.append(" AND dimensions.dimension = ?");
        if(data.item != null) query.append(" AND items.item = ?");
        //Amount is not included as we never filter by amount
        if(data.source != null) query.append(" AND sources.source = ? AND sources.sourceDesc = ?");
        query.append(" ORDER BY timestamp");
    }

    @Override
    protected void appendJoinsToSQL(StringBuilder query) {
        query.append(
                " JOIN positions ON blockItemTransactions.posID = positions.posID" +
                " JOIN dimensions ON blockItemTransactions.dimID = dimensions.dimID" +
                " JOIN items ON blockItemTransactions.itemID = items.itemID" +
                " JOIN sources ON blockItemTransactions.sourceID = sources.sourceID"
        );
    }

    @Override
    protected int setRetrievePreparedStatementValues(PreparedStatement preparedStatement, BlockItemTransactionData data) throws SQLException {
        int index = 1;
        if(data.position != null) {
            preparedStatement.setInt(index++, data.position.getX());
            preparedStatement.setInt(index++, data.position.getY());
            preparedStatement.setInt(index++, data.position.getZ());
        }
        if(data.dimension != null) preparedStatement.setString(index++, data.dimension);
        if(data.item != null) preparedStatement.setString(index++, data.item);
        //Amount is not included as we never filter by amount
        if(data.source != null){
            preparedStatement.setString(index++, data.source.source());
            preparedStatement.setString(index++, data.source.sourceDesc());
        }
        return index;
    }

    @Override
    protected BlockItemTransactionData mapDataFromResultSet(ResultSet set) throws SQLException {
        return new BlockItemTransactionData(
                set.getTimestamp("timestamp"),
                new BlockPos(set.getInt("x"), set.getInt("y"), set.getInt("z")),
                set.getString("dimension"),
                set.getString("item"),
                set.getInt("amount"),
                new SourceData(set.getString("source"), set.getString("sourceDesc"))
        );
    }
}
