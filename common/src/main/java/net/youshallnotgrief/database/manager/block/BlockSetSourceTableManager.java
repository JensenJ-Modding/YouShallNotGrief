package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetSourceTableManager  implements TableManager<BlockSetData>{

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Sources (source, sourceDesc) " +
                "VALUES (?, ?) ON CONFLICT(source, sourceDesc) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Sources " +
                "(sourceID INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT NOT NULL, sourceDesc TEXT NOT NULL, UNIQUE(source, sourceDesc));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setString(1, data.blockSetSourceData().source());
        preparedStatement.setString(2, data.blockSetSourceData().sourceDesc());
    }
}
