package net.youshallnotgrief.database.manager.block;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.database.manager.AbstractDataManager;
import net.youshallnotgrief.util.DatabaseUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class BlockSetDataManager extends AbstractDataManager<BlockSetData, BlockSetQueryData> {

    private static final HashMap<BlockPos, Integer> POSITION_CACHE = new HashMap<>();
    private static final HashMap<String, Integer> BLOCK_CACHE = new HashMap<>();
    private static final HashMap<String, Integer> CAUSE_CACHE = new HashMap<>();
    private static final HashMap<String, Integer> SOURCE_CACHE = new HashMap<>();


    @Override
    public void registerForeignTables() {
        TABLE_MANAGERS.add(new BlockSetDimensionTableManager());
        TABLE_MANAGERS.add(new BlockSetPosTableManager());
        TABLE_MANAGERS.add(new BlockSetBlockTableManager());
        TABLE_MANAGERS.add(new BlockSetCauseTableManager());
        TABLE_MANAGERS.add(new BlockSetSourceTableManager());
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet (posID, timestamp, oldBlockID, newBlockID, causeID, sourceID) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
    }

    @Override
    public String getQuerySQL() {
        return "SELECT blockSet_Positions.x, blockSet_Positions.y, blockSet_Positions.z, blockSet_Dimensions.dimension, " +
                "timestamp, oldBlock.blockName AS oldBlockName, newBlock.blockName AS newBlockName, blockSet_Causes.cause, blockSet_Sources.source, blockSet_Sources.sourceDesc " +
                "FROM blockSet " +
                "JOIN blockSet_Positions ON blockSet.posID = blockSet_Positions.posID " +
                "JOIN blockSet_Dimensions ON blockSet_Positions.dimID = blockSet_Dimensions.dimID " +
                "JOIN blockSet_Blocks AS oldBlock ON blockSet.oldBlockID = oldBlock.blockID " +
                "JOIN blockSet_Blocks AS newBlock ON blockSet.newBlockID = newBlock.blockID " +
                "JOIN blockSet_Causes ON blockSet.causeID = blockSet_Causes.causeID " +
                "JOIN blockSet_Sources ON blockSet.sourceID = blockSet_Sources.sourceID " +
                "WHERE blockSet_Positions.x = ? AND blockSet_Positions.y = ? AND blockSet_Positions.z = ? AND blockSet_Dimensions.dimension = ? " +
                "ORDER BY timestamp DESC " +
                "LIMIT ? OFFSET ?;";
    }

    @Override
    public String getCountSQL() {
        return "SELECT COUNT(*) from blockSet " +
                "JOIN blockSet_Positions ON blockSet.posID = blockSet_Positions.posID " +
                "JOIN blockSet_Dimensions ON blockSet_Positions.dimID = blockSet_Dimensions.dimID " +
                "JOIN blockSet_Blocks AS oldBlock ON blockSet.oldBlockID = oldBlock.blockID " +
                "JOIN blockSet_Blocks AS newBlock ON blockSet.newBlockID = newBlock.blockID " +
                "JOIN blockSet_Causes ON blockSet.causeID = blockSet_Causes.causeID " +
                "JOIN blockSet_Sources ON blockSet.sourceID = blockSet_Sources.sourceID " +
                "WHERE blockSet_Positions.x = ? AND blockSet_Positions.y = ? AND blockSet_Positions.z = ? AND blockSet_Dimensions.dimension = ? ";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, posID INTEGER NOT NULL, timestamp DATETIME NOT NULL, oldBlockID INTEGER NOT NULL, newBlockID INTEGER NOT NULL, causeID INTEGER NOT NULL, sourceID INTEGER NOT NULL, " +
                "FOREIGN KEY (posID) REFERENCES blockSet_Position(posID)," +
                "FOREIGN KEY (oldBlockID) REFERENCES blockSet_Blocks(blockID)," +
                "FOREIGN KEY (newBlockID) REFERENCES blockSet_Blocks(blockID)," +
                "FOREIGN KEY (causeID) REFERENCES blockSet_Causes(causeID)," +
                "FOREIGN KEY (sourceID) REFERENCES blockSet_Sources(sourceID));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {

        int posID = getPositionID(data.pos(), data.dimension());
        if(posID == -1){
            return;
        }

        int oldBlockID = getBlockID(data.oldBlock());
        if(oldBlockID == -1){
            return;
        }

        int newBlockID = getBlockID(data.newBlock());
        if(newBlockID == -1){
            return;
        }

        int causeID = getCauseID(data.cause());
        if(causeID == -1){
            return;
        }

        int sourceID = getSourceID(data.source(), data.sourceDesc());
        if(sourceID == -1){
            return;
        }

        preparedStatement.setInt(1, posID);
        preparedStatement.setTimestamp(2, data.time());
        preparedStatement.setInt(3, oldBlockID);
        preparedStatement.setInt(4, newBlockID);
        preparedStatement.setInt(5, causeID);
        preparedStatement.setInt(6, sourceID);
    }

    @Override
    public void onInsertionCompleted() {
        POSITION_CACHE.clear();
        BLOCK_CACHE.clear();
        SOURCE_CACHE.clear();
        //Don't clear cause cache as there is not many types of cause, so we can keep it in memory
    }

    @Override
    public void clearCache() {
        POSITION_CACHE.clear();
        BLOCK_CACHE.clear();
        CAUSE_CACHE.clear();
        SOURCE_CACHE.clear();
    }

    @Override
    public void setQueryPreparedStatementValues(PreparedStatement preparedStatement, BlockSetQueryData data, int limit, int offset) throws SQLException {
        preparedStatement.setInt(1, data.pos().getX());
        preparedStatement.setInt(2, data.pos().getY());
        preparedStatement.setInt(3, data.pos().getZ());
        preparedStatement.setString(4, data.dimension());
        preparedStatement.setInt(5, limit);
        preparedStatement.setInt(6, offset);
    }

    @Override
    public void setCountPreparedStatementValues(PreparedStatement preparedStatement, BlockSetQueryData data) throws SQLException {
        preparedStatement.setInt(1, data.pos().getX());
        preparedStatement.setInt(2, data.pos().getY());
        preparedStatement.setInt(3, data.pos().getZ());
        preparedStatement.setString(4, data.dimension());
    }

    @Override
    public BlockSetData mapDataFromResultSet(ResultSet resultSet) throws SQLException {
        return new BlockSetData(
                new BlockPos(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")),
                resultSet.getString("dimension"),
                resultSet.getTimestamp("timestamp"),
                resultSet.getString("oldBlockName"),
                resultSet.getString("newBlockName"),
                resultSet.getString("cause"),
                resultSet.getString("source"),
                resultSet.getString("sourceDesc")
        );
    }

    private int getPositionID(BlockPos pos, String dimension) {
        int dimID = BlockSetPosTableManager.getDimensionID(dimension);
        if(dimID == -1){
            return -1;
        }

        String posQuery = "SELECT posID, x, y, z, dimID FROM blockSet_Positions WHERE x = ? AND y = ? AND z = ? AND dimID = ?";
        return DatabaseUtils.getForeignID(pos, posQuery, (statement) -> {
            statement.setInt(1, pos.getX());
            statement.setInt(2, pos.getY());
            statement.setInt(3, pos.getZ());
            statement.setInt(4, dimID);
        }, POSITION_CACHE);
    }

    private int getBlockID(String blockName) {
        String blockQuery = "SELECT blockID, blockName FROM blockSet_Blocks WHERE blockName = ?;";
        return DatabaseUtils.getForeignID(blockName, blockQuery, (statement) -> statement.setString(1, blockName), BLOCK_CACHE);
    }

    private int getCauseID(String cause) {
        String causeQuery = "SELECT causeID, cause FROM blockSet_Causes WHERE cause = ?;";
        return DatabaseUtils.getForeignID(cause, causeQuery, (statement) -> statement.setString(1, cause), CAUSE_CACHE);
    }

    private int getSourceID(String source, String sourceDesc) {
        String sourceQuery = "SELECT sourceID, source, sourceDesc FROM blockSet_Sources WHERE source = ? AND sourceDesc = ?;";
        return DatabaseUtils.getForeignID(source + ", " + sourceDesc, sourceQuery, (statement) -> {
            statement.setString(1, source);
            statement.setString(2, sourceDesc);
        }, SOURCE_CACHE);
    }
}
