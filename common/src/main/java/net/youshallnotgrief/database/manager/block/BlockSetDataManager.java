package net.youshallnotgrief.database.manager.block;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.database.manager.AbstractDataManager;

import java.sql.Connection;
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
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            YouShallNotGriefMod.LOGGER.error("Failed to get posID for position {} in {} when inserting blockset. Database connection failed.", data.pos(), data.dimension());
            return -1;
        }

        int dimID = BlockSetPosTableManager.getDimensionID(data.dimension());
        if(dimID == -1){
            return -1;
        }

        return POSITION_CACHE.computeIfAbsent(data.pos(), (BlockPos position)-> {
            String query = "SELECT posID, x, y, z, dimID FROM blockSet_Positions WHERE x = ? AND y = ? AND z = ? AND dimID = ?";
            try {
                PreparedStatement queryStatement = database.prepareStatement(query);
                queryStatement.setInt(1, position.getX());
                queryStatement.setInt(2, position.getY());
                queryStatement.setInt(3, position.getZ());
                queryStatement.setInt(4, dimID);
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("posID");
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get posID for position {} in {} when inserting blockset. ", data.pos(), data.dimension());
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(query);
            }

            return -1;
        });
    }

    private int getBlockID(BlockSetBlockData data) {
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            YouShallNotGriefMod.LOGGER.error("Failed to get blockID for blockInternalName {} when inserting blockset. Database connection failed.", data.blockInternalName());
            return -1;
        }

        return BLOCK_CACHE.computeIfAbsent(data.blockInternalName(), (String blockInternalName)-> {
            String query = "SELECT blockID, blockInternalName, blockName FROM blockSet_Blocks WHERE blockInternalName = ? AND blockName = ?;";
            try {
                PreparedStatement queryStatement = database.prepareStatement(query);
                queryStatement.setString(1, data.blockInternalName());
                queryStatement.setString(2, data.blockName());
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("blockID");
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get blockID for blockInternalName {} when inserting blockset. ", blockInternalName);
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(query);
            }

            return -1;
        });
    }

    private int getActionID(BlockSetAction action) {
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            YouShallNotGriefMod.LOGGER.error("Failed to get actionID for action {} when inserting blockset. Database connection failed.", action);
            return -1;
        }

        return ACTION_CACHE.computeIfAbsent(String.valueOf(action), (String actionName)-> {
            String query = "SELECT actionID, action FROM blockSet_Actions WHERE action = ?;";
            try {
                PreparedStatement queryStatement = database.prepareStatement(query);
                queryStatement.setString(1, actionName);
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("actionID");
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get actionID for action {} when inserting blockset. ", actionName);
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(query);
            }

            return -1;
        });
    }

    private int getSourceID(BlockSetSourceData data) {
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            YouShallNotGriefMod.LOGGER.error("Failed to get sourceID for source {} when inserting blockset. Database connection failed.", data.source());
            return -1;
        }

        return SOURCE_CACHE.computeIfAbsent(data.source(), (String sourceData)-> {
            String query = "SELECT sourceID, source, sourceDesc FROM blockSet_Sources WHERE source = ? AND sourceDesc = ?;";
            try {
                PreparedStatement queryStatement = database.prepareStatement(query);
                queryStatement.setString(1, data.source());
                queryStatement.setString(2, data.sourceDesc());
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("sourceID");
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get sourceID for source {} when inserting blockset. ", data.source());
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(query);
            }

            return -1;
        });
    }
}
