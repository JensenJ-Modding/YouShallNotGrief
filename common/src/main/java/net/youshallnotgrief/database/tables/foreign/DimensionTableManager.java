package net.youshallnotgrief.database.tables.foreign;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.youshallnotgrief.database.manager.ForeignTableManager;

public class DimensionTableManager extends ForeignTableManager<String> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS dimensions "
                + "(dimID INTEGER PRIMARY KEY, dimension TEXT NOT NULL, UNIQUE(dimension));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO dimensions (dimension) " + "VALUES (?) ON CONFLICT(dimension) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT dimID FROM dimensions WHERE dimension = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, String dimension)
            throws SQLException {
        preparedStatement.setString(1, dimension);
    }
}
