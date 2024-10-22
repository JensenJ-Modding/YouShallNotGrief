package net.youshallnotgrief.database.manager;

import net.youshallnotgrief.database.manager.block.TableManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Future;

public interface DataManager<InsertData, QueryData> {
    void addToDatabase(InsertData data);
    Future<ArrayList<InsertData>> retrieveFromDatabase(QueryData data);
    void commitQueuedToDatabase();

    //Order of registration matters, it is the order in which foreign tables are added into the database.
    void registerForeignTables();
    ArrayList<TableManager<InsertData>> getForeignTables();

    String getQuerySQL();
    void setQueryPreparedStatementValues(PreparedStatement preparedStatement, QueryData data) throws SQLException;
    InsertData mapDataFromResultSet(ResultSet set) throws SQLException;

}
