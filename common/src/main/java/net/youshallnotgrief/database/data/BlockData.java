package net.youshallnotgrief.database.data;

import net.minecraft.core.BlockPos;
import net.youshallnotgrief.database.manager.DatabaseManager;

import java.sql.Timestamp;

public class BlockData extends BaseData {

    public BlockPos position;
    public String dimension;
    public String oldBlock;
    public String newBlock;
    public String cause;
    public SourceData source;

    //Used for constructing insertion data
    public BlockData(Timestamp timestamp, BlockPos position, String dimension, String oldBlock, String newBlock, String cause, SourceData source) {
        super(timestamp);
        this.position = position;
        this.dimension = dimension;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.cause = cause;
        this.source = source;
    }

    //Used for constructing query data
    public BlockData(BlockPos position, String dimension) {
        super(null);
        this.position = position;
        this.dimension = dimension;
    }

    @Override
    public void queue() {
        DatabaseManager.BLOCK_DATA_MANAGER.queueData(this);
    }

    @Override
    public void queueForeignTables() {
        DatabaseManager.POSITION_TABLE_MANAGER.queueData(position);
        DatabaseManager.BLOCK_TABLE_MANAGER.queueData(oldBlock);
        DatabaseManager.BLOCK_TABLE_MANAGER.queueData(newBlock);
        DatabaseManager.DIMENSION_TABLE_MANAGER.queueData(dimension);
        DatabaseManager.CAUSE_TABLE_MANAGER.queueData(cause);
        DatabaseManager.SOURCE_TABLE_MANAGER.queueData(source);
    }
}
