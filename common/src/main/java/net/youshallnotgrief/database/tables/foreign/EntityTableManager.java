package net.youshallnotgrief.database.tables.foreign;

import net.youshallnotgrief.database.manager.ForeignTableManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityTableManager extends ForeignTableManager<String> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS entities " +
                "(entityID INTEGER PRIMARY KEY, entity TEXT NOT NULL, " +
                "UNIQUE(entity));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO entities (entity) " +
                "VALUES (?) ON CONFLICT(entity) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT entityID FROM entities WHERE entity = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, String entity) throws SQLException {
        preparedStatement.setString(1, entity);
    }
}
