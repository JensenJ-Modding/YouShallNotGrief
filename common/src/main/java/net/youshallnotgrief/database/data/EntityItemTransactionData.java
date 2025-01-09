package net.youshallnotgrief.database.data;

import net.youshallnotgrief.database.manager.DatabaseManager;

import java.sql.Timestamp;

public class EntityItemTransactionData extends BaseData{

    public String entityUUID;
    public String item;
    public int amount;
    public SourceData source;

    //Used for constructing insertion data
    public EntityItemTransactionData(Timestamp timestamp, String entityUUID, String item, int amount, SourceData source) {
        super(timestamp);
        this.entityUUID = entityUUID;
        this.item = item;
        this.amount = amount;
        this.source = source;
    }

    //Used for constructing query data
    public EntityItemTransactionData(String entityUUID) {
        super(null);
        this.entityUUID = entityUUID;
    }

    @Override
    public void queue() {
        DatabaseManager.ENTITY_ITEM_TRANSACTION_DATA_MANAGER.queueData(this);
    }

    @Override
    public void queueForeignTables() {
        DatabaseManager.ENTITY_TABLE_MANAGER.queueData(entityUUID);
        DatabaseManager.ITEM_TABLE_MANAGER.queueData(item);
        DatabaseManager.SOURCE_TABLE_MANAGER.queueData(source);
    }
}
