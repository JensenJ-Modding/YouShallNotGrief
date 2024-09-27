package net.youshallnotgrief.database.manager;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.data.BlockSetData;
import net.youshallnotgrief.data.BlockSetQueryData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockSetManager extends AbstractDataManager<BlockSetData, BlockSetQueryData> {

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSets (x, y, z, dimension, timestamp, blockName, cause, causeDesc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public String getQuerySQL() {
        return "SELECT x, y, z, dimension, timestamp, blockName, cause, causeDesc " +
                "FROM blockSets " +
                "WHERE x = ? AND y = ? AND z = ? AND dimension = ?";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSets " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER, z INTEGER, dimension TEXT NOT NULL, " +
                "timestamp DATETIME NOT NULL, blockName TEXT NOT NULL, cause TEXT NOT NULL, causeDesc TEXT NOT NULL);";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setInt(1, data.pos().getX());
        preparedStatement.setInt(2, data.pos().getY());
        preparedStatement.setInt(3, data.pos().getZ());
        preparedStatement.setString(4, data.dimension());
        preparedStatement.setTimestamp(5, data.time());
        preparedStatement.setString(6, data.blockName());
        preparedStatement.setString(7, data.cause());
        preparedStatement.setString(8, data.causeDesc());
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
        return new BlockSetData(
                new BlockPos(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")),
                resultSet.getString("dimension"),
                resultSet.getTimestamp("timestamp"),
                resultSet.getString("blockName"),
                resultSet.getString("cause"),
                resultSet.getString("causeDesc")
        );
    }
}