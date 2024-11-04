package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetCauseTableManager implements TableManager<BlockSetData>{

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Causes (cause) " +
                "VALUES (?) ON CONFLICT(cause) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Causes " +
                "(causeID INTEGER PRIMARY KEY AUTOINCREMENT, cause TEXT NOT NULL, UNIQUE(cause));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setString(1, data.cause());
    }
}
