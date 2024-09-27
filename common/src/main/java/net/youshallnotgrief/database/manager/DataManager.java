package net.youshallnotgrief.database.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DataManager<T, K> {
    void addToDatabase(T data);
    ArrayList<T> retrieveFromDatabase(K data);
    void commitQueuedToDatabase();

    String getCreateTableSQL();
    String getInsertSQL();
    String getQuerySQL();
    void setInsertPreparedStatementValues(PreparedStatement preparedStatement, T data) throws SQLException;
    void setQueryPreparedStatementValues(PreparedStatement preparedStatement, K data) throws SQLException;
    T mapDataFromResultSet(ResultSet set) throws SQLException;

}
