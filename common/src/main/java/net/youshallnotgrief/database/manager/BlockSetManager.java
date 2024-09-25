package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.data.BlockSetData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetManager extends AbstractDataManager<BlockSetData> {

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO blockSets (x, y, z, timestamp, blockName, dimension, cause, causeDesc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSets (id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER, z INTEGER, timestamp DATETIME NOT NULL, blockName TEXT NOT NULL, dimension TEXT NOT NULL, cause TEXT NOT NULL, causeDesc TEXT NOT NULL);";
    }

    @Override
    protected void setPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setInt(1, data.pos().getX());
        preparedStatement.setInt(2, data.pos().getY());
        preparedStatement.setInt(3, data.pos().getZ());
        preparedStatement.setTimestamp(4, data.time());
        preparedStatement.setString(5, data.blockName());
        preparedStatement.setString(6, data.dimension());
        preparedStatement.setString(7, data.cause());
        preparedStatement.setString(8, data.causeDesc());
    }
}
