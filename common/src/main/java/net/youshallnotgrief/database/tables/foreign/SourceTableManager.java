package net.youshallnotgrief.database.tables.foreign;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.youshallnotgrief.database.data.SourceData;
import net.youshallnotgrief.database.manager.ForeignTableManager;

public class SourceTableManager extends ForeignTableManager<SourceData> {

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS sources "
                + "(sourceID INTEGER PRIMARY KEY, source TEXT NOT NULL, sourceDesc TEXT NOT NULL, UNIQUE(source, sourceDesc));";
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO sources (source, sourceDesc) "
                + "VALUES (?, ?) ON CONFLICT(source, sourceDesc) DO NOTHING;";
    }

    @Override
    protected String getCacheQuerySQL() {
        return "SELECT sourceID FROM sources WHERE source = ? AND sourceDesc = ?;";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, SourceData sourceData)
            throws SQLException {
        preparedStatement.setString(1, sourceData.source());
        preparedStatement.setString(2, sourceData.sourceDesc());
    }
}
