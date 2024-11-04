package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetBlockTableManager implements TableManager<BlockSetData>{

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Blocks (blockName) " +
                "VALUES (?), (?) ON CONFLICT(blockName) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Blocks " +
                "(blockID INTEGER PRIMARY KEY AUTOINCREMENT, blockName TEXT NOT NULL, " +
                "UNIQUE(blockName));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData blockSetData) throws SQLException {
        preparedStatement.setString(1, blockSetData.oldBlock());
        preparedStatement.setString(2, blockSetData.newBlock());
    }
}
