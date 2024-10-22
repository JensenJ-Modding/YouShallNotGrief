package net.youshallnotgrief.database.manager.block;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.data.block.BlockSetData;
import net.youshallnotgrief.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        preparedStatement.setInt(1, data.blockSetPosData().pos().getX());
        preparedStatement.setInt(2, data.blockSetPosData().pos().getY());
        preparedStatement.setInt(3, data.blockSetPosData().pos().getZ());

        int dimID = getDimensionID(data.blockSetPosData().dimension());
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
        Connection database = DatabaseManager.getDatabaseConnection();
        if(database == null){
            YouShallNotGriefMod.LOGGER.error("Failed to get dimensionID for dimension {} when inserting block position. Database connection failed.", dimension);
            return -1;
        }

        return DIMENSION_CACHE.computeIfAbsent(dimension, (String dim)-> {
            String dimensionQuery = "SELECT dimID, dimension FROM blockSet_Dimensions WHERE dimension = ?";
            try {
                PreparedStatement queryStatement = database.prepareStatement(dimensionQuery);
                queryStatement.setString(1, dim);
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("dimID");
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get dimensionID for dimension {} when inserting block position. ", dim);
                YouShallNotGriefMod.LOGGER.error(e.toString());
            }

            return -1;
        });
    }
}