package net.youshallnotsteal.database;

import net.youshallnotsteal.YouShallNotStealMod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseManager {

    public static Connection getDatabaseConnection(){
        Connection connection = null;
        if(Objects.equals(YouShallNotStealMod.DatabaseWorldPath, null)){
            return connection;
        }
        try {
            String databasePath = "jdbc:sqlite:" + YouShallNotStealMod.DatabaseWorldPath + "youshallnotsteal.db";
            connection = DriverManager.getConnection(databasePath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
