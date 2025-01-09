package net.youshallnotgrief.database.tables.foreign;

import net.youshallnotgrief.database.manager.ForeignTableManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockTableManager extends ForeignTableManager<String> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blocks " +
                "(blockID INTEGER PRIMARY KEY, block TEXT NOT NULL, " +
                "UNIQUE(block));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blocks (block) " +
                "VALUES (?) ON CONFLICT(block) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT blockID FROM blocks WHERE block = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, String block) throws SQLException {
        preparedStatement.setString(1, block);
    }
}
