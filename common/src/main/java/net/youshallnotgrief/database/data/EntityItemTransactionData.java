package net.youshallnotgrief.database.data;

import net.minecraft.network.chat.Component;
import net.youshallnotgrief.database.manager.DatabaseManager;
import net.youshallnotgrief.util.InventoryUtils;

import java.sql.Timestamp;

public class EntityItemTransactionData extends BaseData{

    public String entityUUID;
    public String item;
    public int count;
    public int amount;
    public SourceData source;

    //Used for constructing insertion data
    public EntityItemTransactionData(Timestamp timestamp, int count, String entityUUID, String item, int amount, SourceData source) {
        super(timestamp);
        this.entityUUID = entityUUID;
        this.item = item;
        this.count = count;
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

    @Override
    public Component formatDataForInspection() {
        return InventoryUtils.formatDataForInspection(timestamp, count, item, amount, source);
    }
}
