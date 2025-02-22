package net.youshallnotgrief.database.manager;

import net.minecraft.world.level.Level;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.data.BaseData;
import net.youshallnotgrief.database.tables.BlockDataManager;
import net.youshallnotgrief.database.tables.BlockItemTransactionDataManager;
import net.youshallnotgrief.database.tables.EntityItemTransactionDataManager;
import net.youshallnotgrief.database.tables.foreign.*;
import net.youshallnotgrief.inspection.InspectionMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseManager {

    private static int QUEUE_SIZE = 0;
    protected static int MAX_QUEUE_SIZE = 50;
    private static final AtomicBoolean isCommitting = new AtomicBoolean(false);
    //TODO: possibly update in future so that it's a map of player to query, allowing multiple people to query at once, this mainly exists to stop queueing loads of queries to the DB, if players are unaware it's loading.
    public static final AtomicBoolean isQuerying = new AtomicBoolean(false);

    private static final ArrayList<DataManager<?>> DATA_MANAGERS = new ArrayList<>();
    public static final BlockDataManager BLOCK_DATA_MANAGER = registerDataManager(new BlockDataManager());
    public static final BlockItemTransactionDataManager BLOCK_ITEM_TRANSACTION_DATA_MANAGER = registerDataManager(new BlockItemTransactionDataManager());
    public static final EntityItemTransactionDataManager ENTITY_ITEM_TRANSACTION_DATA_MANAGER = registerDataManager(new EntityItemTransactionDataManager());

    private static final ArrayList<ForeignTableManager<?>> TABLE_MANAGERS = new ArrayList<>();
    public static final PositionTableManager POSITION_TABLE_MANAGER = registerTableManager(new PositionTableManager());
    public static final DimensionTableManager DIMENSION_TABLE_MANAGER = registerTableManager(new DimensionTableManager());
    public static final BlockTableManager BLOCK_TABLE_MANAGER = registerTableManager(new BlockTableManager());
    public static final ItemTableManager ITEM_TABLE_MANAGER = registerTableManager(new ItemTableManager());
    public static final EntityTableManager ENTITY_TABLE_MANAGER = registerTableManager(new EntityTableManager());
    public static final CauseTableManager CAUSE_TABLE_MANAGER = registerTableManager(new CauseTableManager());
    public static final SourceTableManager SOURCE_TABLE_MANAGER = registerTableManager(new SourceTableManager());

    protected static <Table extends ForeignTableManager<?>> Table registerTableManager(Table manager){
        TABLE_MANAGERS.add(manager);
        return manager;
    }

    protected static <Table extends DataManager<?>> Table registerDataManager(Table manager){
        DATA_MANAGERS.add(manager);
        return manager;
    }

    public static void addToDatabase(BaseData data, Level level){
        if(level.isClientSide()){
            throw new IllegalStateException("Failed to add data to Database Queue. addToDatabase called from clientside." + data);
        }
        QUEUE_SIZE++;
        data.queueForeignTables();
        data.queue();

        if((QUEUE_SIZE >= MAX_QUEUE_SIZE || !InspectionMode.INSPECTING_PLAYERS.isEmpty())) {
            commitQueuedToDatabase();
        }
    }

    public static void commitQueuedToDatabase(){
        try {
            if(QUEUE_SIZE == 0){
                return;
            }

            if(isCommitting.compareAndSet(false, true)) {
                if (DatabaseLifecycleManager.executorService == null) {
                    return;
                }

                TABLE_MANAGERS.forEach(TableManager::prepareToCommitTable);
                DATA_MANAGERS.forEach(TableManager::prepareToCommitTable);

                QUEUE_SIZE = 0;

                DatabaseLifecycleManager.executorService.submit(() -> {
                    Connection database = DatabaseLifecycleManager.getDatabaseConnection();
                    if (database == null) {
                        isCommitting.set(false);
                        return;
                    }

                    TABLE_MANAGERS.forEach(TableManager::commitTable);
                    DATA_MANAGERS.forEach(TableManager::commitTable);

                    isCommitting.set(false);
                });
            }
        } catch (RejectedExecutionException e){
            YouShallNotGriefMod.LOGGER.error("Failed to commit queued data to database. Task could not be scheduled.");
            YouShallNotGriefMod.LOGGER.error(e.toString());
        }
    }

    public static void clearCaches(){
        TABLE_MANAGERS.forEach(ForeignTableManager::clearCache);
    }

    protected static void createTables(){
        TABLE_MANAGERS.forEach((manager) -> createTableIfNotExists(manager.getCreateTableSQL()));
        DATA_MANAGERS.forEach((manager) -> createTableIfNotExists(manager.getCreateTableSQL()));
    }

    private static void createTableIfNotExists(String createTableSQL){
        Connection database = DatabaseLifecycleManager.getDatabaseConnection();
        if(database == null) {
            return;
        }
        try (PreparedStatement preparedStatement = database.prepareStatement(createTableSQL)){
            preparedStatement.execute();
            database.commit();
        }catch(SQLException e){
            YouShallNotGriefMod.LOGGER.error(e.toString());
            YouShallNotGriefMod.LOGGER.error(createTableSQL);
            try {
                database.rollback();
            } catch (SQLException ex) {
                YouShallNotGriefMod.LOGGER.error(ex.toString());
            }
        }
    }
}
