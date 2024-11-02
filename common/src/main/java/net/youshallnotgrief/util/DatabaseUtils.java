package net.youshallnotgrief.util;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseUtils {
    public static <T> int getForeignID(T key, String query, ThrowableBiConsumer<PreparedStatement, T> parameterSetter, Map<T, Integer> map) {
        Connection database = DatabaseManager.getDatabaseConnection();
        if (database == null) {
            YouShallNotGriefMod.LOGGER.error("Failed to get ID for {} when inserting. Database connection failed.", key);
            YouShallNotGriefMod.LOGGER.error(query);
            return -1;
        }

        return map.computeIfAbsent(key, (T k) -> {
            try (PreparedStatement queryStatement = database.prepareStatement(query)) {
                parameterSetter.accept(queryStatement, key);
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get ID for {} when inserting blockset.", key);
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(query);
            }

            return -1;
        });
    }
}
