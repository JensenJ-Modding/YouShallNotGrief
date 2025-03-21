package net.youshallnotgrief.database.data;

import java.sql.Timestamp;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import net.youshallnotgrief.database.manager.DatabaseManager;
import net.youshallnotgrief.util.InventoryUtils;

public class BlockItemTransactionData extends BaseData {

    public BlockPos position;
    public String dimension;
    public String item;
    public int count;
    public int amount;
    public SourceData source;

    // Used for constructing insertion data
    public BlockItemTransactionData(
            Timestamp timestamp,
            int count,
            BlockPos position,
            String dimension,
            String item,
            int amount,
            SourceData source) {
        super(timestamp);
        this.position = position;
        this.dimension = dimension;
        this.count = count;
        this.item = item;
        this.amount = amount;
        this.source = source;
    }

    // Used for constructing query data
    public BlockItemTransactionData(BlockPos position, String dimension) {
        super(null);
        this.position = position;
        this.dimension = dimension;
    }

    @Override
    public void queue() {
        DatabaseManager.BLOCK_ITEM_TRANSACTION_DATA_MANAGER.queueData(this);
    }

    @Override
    public void queueForeignTables() {
        DatabaseManager.POSITION_TABLE_MANAGER.queueData(position);
        DatabaseManager.DIMENSION_TABLE_MANAGER.queueData(dimension);
        DatabaseManager.ITEM_TABLE_MANAGER.queueData(item);
        DatabaseManager.SOURCE_TABLE_MANAGER.queueData(source);
    }

    @Override
    public Component formatDataForInspection() {
        return InventoryUtils.formatDataForInspection(timestamp, count, item, amount, source);
    }
}
