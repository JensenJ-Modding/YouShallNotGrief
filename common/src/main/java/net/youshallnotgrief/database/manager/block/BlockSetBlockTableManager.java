package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetBlockTableManager implements TableManager<BlockSetData>{

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Blocks (blockInternalName, blockName) " +
                "VALUES (?, ?) ON CONFLICT(blockInternalName, blockName) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Blocks " +
                "(blockID INTEGER PRIMARY KEY AUTOINCREMENT, blockInternalName TEXT NOT NULL, blockName TEXT NOT NULL, " +
                "UNIQUE(blockInternalName, blockName));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData blockSetData) throws SQLException {
        preparedStatement.setString(1, blockSetData.blockSetBlockData().blockInternalName());
        preparedStatement.setString(2, blockSetData.blockSetBlockData().blockName());
    }
}
