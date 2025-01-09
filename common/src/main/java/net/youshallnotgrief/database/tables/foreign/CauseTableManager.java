package net.youshallnotgrief.database.tables.foreign;

import net.youshallnotgrief.database.manager.ForeignTableManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CauseTableManager extends ForeignTableManager<String> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS causes " +
                "(causeID INTEGER PRIMARY KEY, cause TEXT NOT NULL, UNIQUE(cause));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO causes (cause) " +
                "VALUES (?) ON CONFLICT(cause) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT causeID FROM causes WHERE cause = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, String cause) throws SQLException {
        preparedStatement.setString(1, cause);
    }
}
