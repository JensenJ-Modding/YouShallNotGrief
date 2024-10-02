package net.youshallnotgrief.database.manager.block;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface TableManager<InsertData> {
    String getInsertSQL();
    String getCreateTableSQL();
    void setInsertPreparedStatementValues(PreparedStatement preparedStatement, InsertData data) throws SQLException;
}