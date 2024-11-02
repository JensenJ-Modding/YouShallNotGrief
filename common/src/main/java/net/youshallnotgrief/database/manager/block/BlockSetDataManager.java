package net.youshallnotgrief.database.manager.block;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.YouShallNotGriefMod;
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
    private static final HashMap<String, Integer> ACTION_CACHE = new HashMap<>();
    private static final HashMap<String, Integer> SOURCE_CACHE = new HashMap<>();


    @Override
    public void registerForeignTables() {
        TABLE_MANAGERS.add(new BlockSetDimensionTableManager());
        TABLE_MANAGERS.add(new BlockSetPosTableManager());
        TABLE_MANAGERS.add(new BlockSetBlockTableManager());
        TABLE_MANAGERS.add(new BlockSetActionTableManager());
        TABLE_MANAGERS.add(new BlockSetSourceTableManager());
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet (posID, timestamp, blockID, actionID, sourceID) " +
                "VALUES (?, ?, ?, ?, ?);";
    }

    @Override
    public String getQuerySQL() {
        return "SELECT blockSet_Positions.x, blockSet_Positions.y, blockSet_Positions.z, blockSet_Dimensions.dimension, " +
                "timestamp, blockSet_Blocks.blockInternalName, blockSet_Blocks.blockName, blockSet_Actions.action, blockSet_Sources.source, blockSet_Sources.sourceDesc " +
                "FROM blockSet " +
                "JOIN blockSet_Positions ON blockSet.posID = blockSet_Positions.posID " +
                "JOIN blockSet_Dimensions ON blockSet_Positions.dimID = blockSet_Dimensions.dimID " +
                "JOIN blockSet_Blocks ON blockSet.blockID = blockSet_Blocks.blockID " +
                "JOIN blockSet_Actions ON blockSet.actionID = blockSet_Actions.actionID " +
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
                "JOIN blockSet_Blocks ON blockSet.blockID = blockSet_Blocks.blockID " +
                "JOIN blockSet_Actions ON blockSet.actionID = blockSet_Actions.actionID " +
                "JOIN blockSet_Sources ON blockSet.sourceID = blockSet_Sources.sourceID " +
                "WHERE blockSet_Positions.x = ? AND blockSet_Positions.y = ? AND blockSet_Positions.z = ? AND blockSet_Dimensions.dimension = ? ";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, posID INTEGER NOT NULL, timestamp DATETIME NOT NULL, blockID INTEGER NOT NULL, actionID INTEGER NOT NULL, sourceID INTEGER NOT NULL, " +
                "FOREIGN KEY (posID) REFERENCES blockSet_Position(posID)," +
                "FOREIGN KEY (blockID) REFERENCES blockSet_Block(blockID)," +
                "FOREIGN KEY (actionID) REFERENCES blockSet_Action(actionID)," +
                "FOREIGN KEY (sourceID) REFERENCES blockSet_Sources(sourceID));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {

        int posID = getPositionID(data.blockSetPosData());
        if(posID == -1){
            return;
        }

        int blockID = getBlockID(data.blockSetBlockData());
        if(blockID == -1){
            return;
        }

        int actionID = getActionID(data.action());
        if(actionID == -1){
            return;
        }

        int sourceID = getSourceID(data.blockSetSourceData());
        if(sourceID == -1){
            return;
        }

        preparedStatement.setInt(1, posID);
        preparedStatement.setTimestamp(2, data.time());
        preparedStatement.setInt(3, blockID);
        preparedStatement.setInt(4, actionID);
        preparedStatement.setInt(5, sourceID);
    }

    @Override
    public void onInsertionCompleted() {
        POSITION_CACHE.clear();
        BLOCK_CACHE.clear();
        SOURCE_CACHE.clear();
        //Don't clear action cache as there is not many types of action, so we can keep it in memory
    }

    @Override
    public void clearCache() {
        POSITION_CACHE.clear();
        BLOCK_CACHE.clear();
        ACTION_CACHE.clear();
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
        try{
            return new BlockSetData(new BlockSetPosData(
                    new BlockPos(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")),
                    resultSet.getString("dimension")),
                    resultSet.getTimestamp("timestamp"),
                    new BlockSetBlockData(resultSet.getString("blockInternalName"), resultSet.getString("blockName")),
                    BlockSetAction.valueOf(resultSet.getString("action")),
                    new BlockSetSourceData(resultSet.getString("source"), resultSet.getString("sourceDesc"))
            );
        }catch (IllegalArgumentException e){
            YouShallNotGriefMod.LOGGER.error("BlockSetAction not recognised when querying database. Defaulting to SET.");
            return new BlockSetData(new BlockSetPosData(
                    new BlockPos(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")), resultSet.getString("dimension")),
                    resultSet.getTimestamp("timestamp"),
                    new BlockSetBlockData(resultSet.getString("blockInternalName"), resultSet.getString("blockName")),
                    BlockSetAction.SET,
                    new BlockSetSourceData(resultSet.getString("source"), resultSet.getString("sourceDesc"))
            );
        }
    }

    private int getPositionID(BlockSetPosData data) {
        int dimID = BlockSetPosTableManager.getDimensionID(data.dimension());
        if(dimID == -1){
            return -1;
        }

        String posQuery = "SELECT posID, x, y, z, dimID FROM blockSet_Positions WHERE x = ? AND y = ? AND z = ? AND dimID = ?";
        return DatabaseUtils.getForeignID(data.pos(), posQuery, (statement, pos) -> {
            statement.setInt(1, pos.getX());
            statement.setInt(2, pos.getY());
            statement.setInt(3, pos.getZ());
            statement.setInt(4, dimID);
        }, POSITION_CACHE);
    }

    private int getBlockID(BlockSetBlockData data) {
        String blockQuery = "SELECT blockID, blockInternalName, blockName FROM blockSet_Blocks WHERE blockInternalName = ? AND blockName = ?;";
        return DatabaseUtils.getForeignID(data.blockInternalName(), blockQuery, (statement, block) -> {
            statement.setString(1, data.blockInternalName());
            statement.setString(2, data.blockName());
        }, BLOCK_CACHE);
    }

    private int getActionID(BlockSetAction action) {
        String actionQuery = "SELECT actionID, action FROM blockSet_Actions WHERE action = ?;";
        return DatabaseUtils.getForeignID(String.valueOf(action), actionQuery, (statement, actionKey) -> statement.setString(1, actionKey), ACTION_CACHE);
    }

    private int getSourceID(BlockSetSourceData data) {
        String sourceQuery = "SELECT sourceID, source, sourceDesc FROM blockSet_Sources WHERE source = ? AND sourceDesc = ?;";
        return DatabaseUtils.getForeignID(data.source(), sourceQuery, (statement, sourceKey) -> {
            statement.setString(1, data.source());
            statement.setString(2, data.sourceDesc());
        }, SOURCE_CACHE);
    }
}
