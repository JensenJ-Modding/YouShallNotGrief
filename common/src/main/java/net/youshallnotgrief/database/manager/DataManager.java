package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.database.manager.block.TableManager;
import net.youshallnotgrief.util.RetrieveResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Future;

public interface DataManager<InsertData, QueryData> {
    void addToDatabase(InsertData data);
    Future<RetrieveResult<InsertData>> retrieveFromDatabase(QueryData data, int limit, int offset);
    void commitQueuedToDatabase();

    //Order of registration matters, it is the order in which foreign tables are added into the database.
    void registerForeignTables();
    ArrayList<TableManager<InsertData>> getForeignTables();

    String getQuerySQL();
    void setQueryPreparedStatementValues(PreparedStatement preparedStatement, QueryData data, int limit, int offset) throws SQLException;
    InsertData mapDataFromResultSet(ResultSet set) throws SQLException;

    String getCountSQL();
    void setCountPreparedStatementValues(PreparedStatement preparedStatement, QueryData data) throws SQLException;
}
