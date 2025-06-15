package net.youshallnotgrief.database.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.MiscUtils;

public abstract class ForeignTableManager<InsertData> extends TableManager<InsertData> {

    private final Cache<InsertData, Integer> dataToDatabaseForeignKeyCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    @Override
    public void onInsertionCompleted() {
        updateCachedForeignKeys();
    }

    private void updateCachedForeignKeys() {
        for (InsertData data : COMMITTING_QUEUED_DATA) {
            if (dataToDatabaseForeignKeyCache.asMap().containsKey(data)) {
                continue;
            }
            mapForeignID(data, dataToDatabaseForeignKeyCache.asMap());
        }
    }

    public void mapForeignID(InsertData key, Map<InsertData, Integer> map) {
        Connection database = DatabaseLifecycleManager.getDatabaseConnection();
        if (database == null) {
            YouShallNotGriefMod.LOGGER.error(
                    "Failed to get ID for {} when inserting. Database connection failed.", key);
            YouShallNotGriefMod.LOGGER.error(getCacheQuerySQL());
            return;
        }

        map.computeIfAbsent(key, (InsertData k) -> {
            try (PreparedStatement queryStatement = database.prepareStatement(getCacheQuerySQL())) {
                setInsertPreparedStatementValues(queryStatement, key);
                try (ResultSet resultSet = queryStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            } catch (SQLException e) {
                YouShallNotGriefMod.LOGGER.error("Failed to get ID for {} when inserting data.", key);
                YouShallNotGriefMod.LOGGER.error(e.toString());
                YouShallNotGriefMod.LOGGER.error(getCacheQuerySQL());
            }

            return null;
        });
    }

    // Should be called when the database is closed, as caches may not be correct with a different world/server.
    public void clearCache() {
        MiscUtils.logIfEnabled(
                "Clearing cache with {} entries",
                dataToDatabaseForeignKeyCache.asMap().size());
        dataToDatabaseForeignKeyCache.asMap().clear();
    }

    public int getForeignKeyInDatabase(InsertData data) {
        // Map the foreign key into cache from the database if it doesn't exist
        mapForeignID(data, dataToDatabaseForeignKeyCache.asMap());
        return Objects.requireNonNullElse(dataToDatabaseForeignKeyCache.asMap().get(data), -1);
    }

    protected abstract String getCacheQuerySQL();
}
