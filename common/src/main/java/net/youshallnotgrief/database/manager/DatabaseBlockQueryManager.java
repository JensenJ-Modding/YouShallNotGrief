package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.data.CombinedBlockData;
import net.youshallnotgrief.inspection.RetrieveResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

//TODO: REFACTOR CLASS
public class DatabaseBlockQueryManager {

    private static int getCountFromDatabase(CombinedBlockData data, Connection database){
        int count = DatabaseManager.BLOCK_DATA_MANAGER.getCountFromDatabase(data.blockData, database);
        count += DatabaseManager.BLOCK_ITEM_TRANSACTION_DATA_MANAGER.getCountFromDatabase(data.blockItemTransactionData, database);
        return count;
    }

    protected static String getRetrieveSQL() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT timestamp, count, positions.x, positions.y, positions.z, dimensions.dimension, ");
        query.append("oldBlock.block AS oldBlock, newBlock.block AS newBlock, ");
        query.append("causes.cause, sources.source, sources.sourceDesc, ");
        query.append("NULL AS item, NULL AS amount ");
        query.append("FROM blockChanges");
        DatabaseManager.BLOCK_DATA_MANAGER.appendJoinsToSQL(query);
        query.append(" WHERE ");
        query.append("positions.x = ? AND positions.y = ? AND positions.z = ? ");
        query.append("AND dimensions.dimension = ? ");
        query.append("UNION ALL ");
        query.append("SELECT timestamp, count, positions.x, positions.y, positions.z, dimensions.dimension, ");
        query.append("NULL AS oldBlock, NULL AS newBlock, ");
        query.append("NULL as cause, sources.source, sources.sourceDesc, ");
        query.append("items.item, amount ");
        query.append("FROM blockItemTransactions");
        DatabaseManager.BLOCK_ITEM_TRANSACTION_DATA_MANAGER.appendJoinsToSQL(query);
        query.append(" WHERE ");
        query.append("positions.x = ? AND positions.y = ? AND positions.z = ? ");
        query.append("AND dimensions.dimension = ? ");
        query.append("ORDER BY timestamp DESC ");
        query.append("LIMIT ? OFFSET ?;");
        return query.toString();
    }

    protected static void setRetrievePreparedStatementValues(PreparedStatement preparedStatement, CombinedBlockData data, int limit, int offset) throws SQLException {
        preparedStatement.setInt(1, data.blockData.position.getX());
        preparedStatement.setInt(2, data.blockData.position.getY());
        preparedStatement.setInt(3, data.blockData.position.getZ());
        preparedStatement.setString(4, data.blockData.dimension);

        preparedStatement.setInt(5, data.blockData.position.getX());
        preparedStatement.setInt(6, data.blockData.position.getY());
        preparedStatement.setInt(7, data.blockData.position.getZ());
        preparedStatement.setString(8, data.blockData.dimension);

        preparedStatement.setInt(9, limit);
        preparedStatement.setInt(10, offset);
    }

    private static CombinedBlockData mapDataFromResultSet(ResultSet set) throws SQLException {
        return new CombinedBlockData(DatabaseManager.BLOCK_DATA_MANAGER.mapDataFromResultSet(set), DatabaseManager.BLOCK_ITEM_TRANSACTION_DATA_MANAGER.mapDataFromResultSet(set));
    }

    public static ArrayList<CombinedBlockData> getDataFromDatabase(CombinedBlockData data, Connection database, int limit, int offset){
        ArrayList<CombinedBlockData> dataToReturn = new ArrayList<>();
        String retrieveQuery = getRetrieveSQL();
        try (PreparedStatement preparedStatement = database.prepareStatement(retrieveQuery)) {
            setRetrievePreparedStatementValues(preparedStatement, data, limit, offset);
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                try {
                    dataToReturn.add(mapDataFromResultSet(set));
                } catch (SQLException e) {
                    YouShallNotGriefMod.LOGGER.error("Error retrieving data from database when performing mapping data from result set.");
                    YouShallNotGriefMod.LOGGER.error(e.toString());
                    YouShallNotGriefMod.LOGGER.error(retrieveQuery);
                    return null;
                }
            }
        } catch (SQLException e) {
            YouShallNotGriefMod.LOGGER.error("Error retrieving data from database:");
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(retrieveQuery);
            return null;
        }
        return dataToReturn;
    }

    //BiConsumer is a function to pass the returned data to, the boolean is whether a task is already running
    public static void retrieveFromDatabase(CombinedBlockData data, int limit, int offset, BiConsumer<Boolean, RetrieveResult<CombinedBlockData>> callback) {
        if(DatabaseLifecycleManager.executorService == null)
            return;

        Callable<RetrieveResult<CombinedBlockData>> task = () -> {
            Connection database = DatabaseLifecycleManager.getDatabaseConnection();
            if (database == null) {
                return null;
            }

            int count = getCountFromDatabase(data, database);
            if(count <= -1){
                return null;
            }
            ArrayList<CombinedBlockData> dataToReturn = getDataFromDatabase(data, database, limit, offset);
            return new RetrieveResult<>(dataToReturn, count);
        };

        if(DatabaseManager.isQuerying.compareAndSet(false, true)) {
            DatabaseLifecycleManager.executorService.submit(() -> {
                try {
                    RetrieveResult<CombinedBlockData> result = task.call();
                    DatabaseManager.isQuerying.set(false);
                    callback.accept(false, result);
                } catch (Exception e) {
                    YouShallNotGriefMod.LOGGER.error("Error getting data from database retrieval:");
                    YouShallNotGriefMod.LOGGER.error(e.toString());
                    DatabaseManager.isQuerying.set(false);
                    callback.accept(false, null);
                }
            });
        }else{
            callback.accept(true, null);
        }
    }
}
