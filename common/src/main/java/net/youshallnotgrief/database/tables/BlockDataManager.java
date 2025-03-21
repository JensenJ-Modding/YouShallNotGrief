package net.youshallnotgrief.database.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minecraft.core.BlockPos;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.BlockData;
import net.youshallnotgrief.database.data.SourceData;
import net.youshallnotgrief.database.manager.DataManager;
import net.youshallnotgrief.database.manager.DatabaseManager;

public class BlockDataManager extends DataManager<BlockData> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockChanges "
                + "(id INTEGER PRIMARY KEY, timestamp DATETIME NOT NULL, lastTimestamp DATETIME NOT NULL, count INTEGER NOT NULL DEFAULT 1, posID INTEGER NOT NULL, dimID INTEGER NOT NULL, oldBlockID INTEGER NOT NULL, newBlockID INTEGER NOT NULL, causeID INTEGER NOT NULL, sourceID INTEGER NOT NULL, "
                + "FOREIGN KEY (posID) REFERENCES positions(posID),"
                + "FOREIGN KEY (dimID) REFERENCES dimensions(dimID),"
                + "FOREIGN KEY (oldBlockID) REFERENCES blocks(blockID),"
                + "FOREIGN KEY (newBlockID) REFERENCES blocks(blockID),"
                + "FOREIGN KEY (causeID) REFERENCES causes(causeID),"
                + "FOREIGN KEY (sourceID) REFERENCES sources(sourceID));";
    }

    @Override
    protected String getRetrieveSQL() {
        return "SELECT timestamp, count, positions.x, positions.y, positions.z, dimensions.dimension, "
                + "oldBlock.block AS oldBlock, newBlock.block AS newBlock, "
                + "causes.cause, sources.source, sources.sourceDesc FROM blockChanges";
    }

    @Override
    protected String getCountSQL() {
        return "SELECT COUNT(*) FROM blockChanges";
    }

    @Override
    protected String getCreateIndexSQL() {
        return "CREATE INDEX IF NOT EXISTS idx_blockChanges_posID ON blockChanges (posID);";
    }

    protected String getInsertSQL() {
        return "INSERT INTO blockChanges (timestamp, lastTimestamp, count, posID, dimID, oldBlockID, newBlockID, causeID, sourceID) "
                + "VALUES (?, ?, 1, ?, ?, ?, ?, ?, ?);";
    }

    protected void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockData data)
            throws SQLException {
        int posID = DatabaseManager.POSITION_TABLE_MANAGER.getForeignKeyInDatabase(data.position);
        int dimID = DatabaseManager.DIMENSION_TABLE_MANAGER.getForeignKeyInDatabase(data.dimension);
        int oldBlockID = DatabaseManager.BLOCK_TABLE_MANAGER.getForeignKeyInDatabase(data.oldBlock);
        int newBlockID = DatabaseManager.BLOCK_TABLE_MANAGER.getForeignKeyInDatabase(data.newBlock);
        int causeID = DatabaseManager.CAUSE_TABLE_MANAGER.getForeignKeyInDatabase(data.cause);
        int sourceID = DatabaseManager.SOURCE_TABLE_MANAGER.getForeignKeyInDatabase(data.source);

        preparedStatement.setTimestamp(1, data.timestamp);
        preparedStatement.setTimestamp(2, data.timestamp);
        preparedStatement.setInt(3, posID);
        preparedStatement.setInt(4, dimID);
        preparedStatement.setInt(5, oldBlockID);
        preparedStatement.setInt(6, newBlockID);
        preparedStatement.setInt(7, causeID);
        preparedStatement.setInt(8, sourceID);
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE blockChanges SET count = count + 1, lastTimestamp = ? "
                + "WHERE posID = ? AND dimID = ? AND oldBlockID = ? AND newBlockID = ? AND causeID = ? AND sourceID = ? AND (? - lastTimestamp) <= ?;";
    }

    @Override
    protected void setUpdatePreparedStatementValues(PreparedStatement preparedStatement, BlockData data)
            throws SQLException {
        int posID = DatabaseManager.POSITION_TABLE_MANAGER.getForeignKeyInDatabase(data.position);
        int dimID = DatabaseManager.DIMENSION_TABLE_MANAGER.getForeignKeyInDatabase(data.dimension);
        int oldBlockID = DatabaseManager.BLOCK_TABLE_MANAGER.getForeignKeyInDatabase(data.oldBlock);
        int newBlockID = DatabaseManager.BLOCK_TABLE_MANAGER.getForeignKeyInDatabase(data.newBlock);
        int causeID = DatabaseManager.CAUSE_TABLE_MANAGER.getForeignKeyInDatabase(data.cause);
        int sourceID = DatabaseManager.SOURCE_TABLE_MANAGER.getForeignKeyInDatabase(data.source);

        preparedStatement.setTimestamp(1, data.timestamp);
        preparedStatement.setInt(2, posID);
        preparedStatement.setInt(3, dimID);
        preparedStatement.setInt(4, oldBlockID);
        preparedStatement.setInt(5, newBlockID);
        preparedStatement.setInt(6, causeID);
        preparedStatement.setInt(7, sourceID);
        preparedStatement.setTimestamp(8, data.timestamp);
        preparedStatement.setInt(9, ServerConfig.databaseMergeTime.get() * 1000);
    }

    @Override
    protected void appendFiltersToSQL(BlockData data, StringBuilder query) {
        if (data.position != null) query.append(" AND positions.x = ? AND positions.y = ? AND positions.z = ?");
        if (data.dimension != null) query.append(" AND dimensions.dimension = ?");
        if (data.oldBlock != null) query.append(" AND oldBlock.block = ?");
        if (data.newBlock != null) query.append(" AND newBlock.block = ?");
        if (data.cause != null) query.append(" AND causes.cause = ?");
        if (data.source != null) query.append(" AND sources.source = ? AND sources.sourceDesc = ?");
        query.append(" ORDER BY timestamp");
    }

    @Override
    public void appendJoinsToSQL(StringBuilder query) {
        query.append(" JOIN positions ON blockChanges.posID = positions.posID"
                + " JOIN dimensions ON blockChanges.dimID = dimensions.dimID"
                + " JOIN blocks AS oldBlock ON blockChanges.oldBlockID = oldBlock.blockID"
                + " JOIN blocks AS newBlock ON blockChanges.newBlockID = newBlock.blockID"
                + " JOIN causes ON blockChanges.causeID = causes.causeID"
                + " JOIN sources ON blockChanges.sourceID = sources.sourceID");
    }

    @Override
    protected int setRetrievePreparedStatementValues(PreparedStatement preparedStatement, BlockData data)
            throws SQLException {
        int index = 1;
        if (data.position != null) {
            preparedStatement.setInt(index++, data.position.getX());
            preparedStatement.setInt(index++, data.position.getY());
            preparedStatement.setInt(index++, data.position.getZ());
        }
        if (data.dimension != null) preparedStatement.setString(index++, data.dimension);
        if (data.oldBlock != null) preparedStatement.setString(index++, data.oldBlock);
        if (data.newBlock != null) preparedStatement.setString(index++, data.newBlock);
        if (data.cause != null) preparedStatement.setString(index++, data.cause);
        if (data.source != null) {
            preparedStatement.setString(index++, data.source.source());
            preparedStatement.setString(index++, data.source.sourceDesc());
        }
        return index;
    }

    @Override
    public BlockData mapDataFromResultSet(ResultSet set) throws SQLException {
        return new BlockData(
                set.getTimestamp("timestamp"),
                set.getInt("count"),
                new BlockPos(set.getInt("x"), set.getInt("y"), set.getInt("z")),
                set.getString("dimension"),
                set.getString("oldBlock"),
                set.getString("newBlock"),
                set.getString("cause"),
                new SourceData(set.getString("source"), set.getString("sourceDesc")));
    }
}
