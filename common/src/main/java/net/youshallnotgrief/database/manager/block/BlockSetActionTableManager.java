package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetActionTableManager implements TableManager<BlockSetData>{

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Actions (action) " +
                "VALUES (?) ON CONFLICT(action) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Actions " +
                "(actionID INTEGER PRIMARY KEY AUTOINCREMENT, action TEXT NOT NULL, UNIQUE(action));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setString(1, data.action().toString());
    }
}
