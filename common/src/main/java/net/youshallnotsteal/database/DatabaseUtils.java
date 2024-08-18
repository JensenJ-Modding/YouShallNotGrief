package net.youshallnotsteal.database;

import net.youshallnotsteal.YouShallNotStealMod;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtils {

    public static boolean createTable(String SQL) {
        try (PreparedStatement preparedStatement = DatabaseManager.databaseConnection.prepareStatement(SQL)){
            preparedStatement.execute();
            DatabaseManager.databaseConnection.commit();
        }catch(SQLException e){
            YouShallNotStealMod.LOGGER.error(e.toString());
            try {
                DatabaseManager.databaseConnection.rollback();
            } catch (SQLException ex) {
                YouShallNotStealMod.LOGGER.error(ex.toString());
            }
            return false;
        }
        return true;
    }
}
