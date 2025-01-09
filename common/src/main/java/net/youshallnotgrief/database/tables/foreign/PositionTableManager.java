package net.youshallnotgrief.database.tables.foreign;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.database.manager.ForeignTableManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PositionTableManager extends ForeignTableManager<BlockPos> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS positions " +
                "(posID INTEGER PRIMARY KEY, x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, " +
                "UNIQUE(x, y, z));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO positions (x, y, z) " +
                "VALUES (?, ?, ?) ON CONFLICT(x, y, z) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT posID FROM positions WHERE x = ? AND y = ? AND z = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockPos blockPos) throws SQLException {
        preparedStatement.setInt(1, blockPos.getX());
        preparedStatement.setInt(2, blockPos.getY());
        preparedStatement.setInt(3, blockPos.getZ());
    }
}