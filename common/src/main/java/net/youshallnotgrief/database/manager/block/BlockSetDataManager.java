package net.youshallnotgrief.database.manager.block;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.database.manager.AbstractDataManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockSetDataManager extends AbstractDataManager<BlockSetData, BlockSetQueryData> {

    @Override
    public void registerForeignTables() {

    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSets (x, y, z, dimension, timestamp, blockName, blockID, action, source, sourceDesc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public String getQuerySQL() {
        return "SELECT x, y, z, dimension, timestamp, blockName, blockID, action, source, sourceDesc " +
                "FROM blockSets " +
                "WHERE x = ? AND y = ? AND z = ? AND dimension = ?";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSets " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, dimension TEXT NOT NULL, " +
                "timestamp DATETIME NOT NULL, blockName TEXT NOT NULL, blockID TEXT NOT NULL, action INTEGER NOT NULL, source TEXT NOT NULL, sourceDesc TEXT NOT NULL);";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setInt(1, data.blockSetPosData().pos().getX());
        preparedStatement.setInt(2, data.blockSetPosData().pos().getY());
        preparedStatement.setInt(3, data.blockSetPosData().pos().getZ());
        preparedStatement.setString(4, data.blockSetPosData().dimension());
        preparedStatement.setTimestamp(5, data.time());
        preparedStatement.setString(6, data.blockSetBlockData().blockName());
        preparedStatement.setString(7, data.blockSetBlockData().blockID());
        preparedStatement.setString(8, data.action().toString());
        preparedStatement.setString(9, data.blockSetSourceData().source());
        preparedStatement.setString(10, data.blockSetSourceData().sourceDesc());
    }

    @Override
    public void setQueryPreparedStatementValues(PreparedStatement preparedStatement, BlockSetQueryData data) throws SQLException {
        preparedStatement.setInt(1, data.pos().getX());
        preparedStatement.setInt(2, data.pos().getY());
        preparedStatement.setInt(3, data.pos().getZ());
        preparedStatement.setString(4, data.dimension());
    }

    @Override
    public BlockSetData mapDataFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            return new BlockSetData(new BlockSetPosData(
                    new BlockPos(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")), resultSet.getString("dimension")),
                    resultSet.getTimestamp("timestamp"),
                    new BlockSetBlockData(resultSet.getString("blockName"), resultSet.getString("blockID")),
                    BlockSetAction.valueOf(resultSet.getString("action")),
                    new BlockSetSourceData(resultSet.getString("source"), resultSet.getString("sourceDesc"))
            );
        }catch (IllegalArgumentException e){
            YouShallNotGriefMod.LOGGER.error("BlockSetAction not recognised when querying database. Defaulting to SET.");
            return new BlockSetData(new BlockSetPosData(
                    new BlockPos(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")), resultSet.getString("dimension")),
                    resultSet.getTimestamp("timestamp"),
                    new BlockSetBlockData(resultSet.getString("blockName"), resultSet.getString("blockID")),
                    BlockSetAction.SET,
                    new BlockSetSourceData(resultSet.getString("source"), resultSet.getString("sourceDesc"))
            );
        }
    }
}
