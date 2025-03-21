package net.youshallnotgrief.database.tables.foreign;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.youshallnotgrief.database.manager.ForeignTableManager;

public class ItemTableManager extends ForeignTableManager<String> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS items " + "(itemID INTEGER PRIMARY KEY, item TEXT NOT NULL, "
                + "UNIQUE(item));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO items (item) " + "VALUES (?) ON CONFLICT(item) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT itemID FROM items WHERE item = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, String item) throws SQLException {
        preparedStatement.setString(1, item);
    }
}
