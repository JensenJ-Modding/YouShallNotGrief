package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.data.block.BlockSetData;
import net.youshallnotgrief.util.DatabaseUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class BlockSetPosTableManager implements TableManager<BlockSetData> {

    private static final HashMap<String, Integer> DIMENSION_CACHE = new HashMap<>();

    @Override
    public String getInsertSQL() {
        return "INSERT INTO blockSet_Positions (x, y, z, dimID) " +
                "VALUES (?, ?, ?, ?) ON CONFLICT(x, y, z, dimID) DO NOTHING;";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS blockSet_Positions " +
                "(posID INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, dimID INTEGER NOT NULL, " +
                "UNIQUE(x, y, z, dimID), " +
                "FOREIGN KEY (dimID) REFERENCES blockSet_Dimensions(dimID));";
    }

    @Override
    public void setInsertPreparedStatementValues(PreparedStatement preparedStatement, BlockSetData data) throws SQLException {
        preparedStatement.setInt(1, data.pos().getX());
        preparedStatement.setInt(2, data.pos().getY());
        preparedStatement.setInt(3, data.pos().getZ());

        int dimID = getDimensionID(data.dimension());
        if(dimID == -1){
            return;
        }
        preparedStatement.setInt(4, dimID);
    }

    @Override
    public void clearCache() {
        DIMENSION_CACHE.clear();
    }

    public static int getDimensionID(String dimension) {
        String dimensionQuery = "SELECT dimID, dimension FROM blockSet_Dimensions WHERE dimension = ?";
        return DatabaseUtils.getForeignID(dimension, dimensionQuery, (statement) -> statement.setString(1, dimension), DIMENSION_CACHE);
    }
}