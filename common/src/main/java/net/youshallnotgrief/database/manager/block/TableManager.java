package net.youshallnotgrief.database.manager.block;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface TableManager<InsertData> {
    String getInsertSQL();
    String getCreateTableSQL();
    void setInsertPreparedStatementValues(PreparedStatement preparedStatement, InsertData data) throws SQLException;
    default void onInsertionCompleted() {}
    //Should be called when the database is closed, as caches may not be correct with a different world/server.
    default void clearCache() {}
}