package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockSetDimensionTableManager implements TableManager<BlockSetData> {

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Dimensions (dimension) " +
                "VALUES (?) ON CONFLICT(dimension) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Dimensions " +
                "(dimID INTEGER PRIMARY KEY AUTOINCREMENT, dimension TEXT NOT NULL, UNIQUE(dimension));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setString(1, data.blockSetPosData().dimension());
    }
}