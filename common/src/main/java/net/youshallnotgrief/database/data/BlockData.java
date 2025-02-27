package net.youshallnotgrief.database.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCause;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.database.manager.DatabaseManager;
import net.youshallnotgrief.inspection.ComponentUtils;
import net.youshallnotgrief.util.MiscUtils;

import java.sql.Timestamp;

public class BlockData extends BaseData {

    public BlockPos position;
    public int count;
    public String dimension;
    public String oldBlock;
    public String newBlock;
    public String cause;
    public SourceData source;

    //Used for constructing insertion data
    public BlockData(Timestamp timestamp, int count, BlockPos position, String dimension, String oldBlock, String newBlock, String cause, SourceData source) {
        super(timestamp);
        this.position = position;
        this.count = count;
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
        DatabaseManager.DIMENSION_TABLE_MANAGER.queueData(dimension);
        DatabaseManager.BLOCK_TABLE_MANAGER.queueData(oldBlock);
        DatabaseManager.BLOCK_TABLE_MANAGER.queueData(newBlock);
        DatabaseManager.CAUSE_TABLE_MANAGER.queueData(cause);
        DatabaseManager.SOURCE_TABLE_MANAGER.queueData(source);
    }

    @Override
    public Component formatDataForInspection() {
        MutableComponent timeComp = ComponentUtils.formatTimeAgo(timestamp)
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionSecondaryColour.get()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(ComponentUtils.formatTime(timestamp))))
                );

        //TODO: FORMAT
        MutableComponent countComp = Component.literal(count + "x ");

        MutableComponent oldBlockComp = ComponentUtils.getBlockComponentFromString(oldBlock);
        MutableComponent newBlockComp = ComponentUtils.getBlockComponentFromString(newBlock);

        BlockSetCause blockSetCause = BlockSetCauses.getCauseFromTag(cause);
        if(blockSetCause == null){
            YouShallNotGriefMod.LOGGER.warn("Tried to show logs for a block cause which does not exist: {}.", blockSetCause);
            return null;
        }

        MutableComponent sourceComp = ComponentUtils.getSourceComponentFromString(source.source(), source.sourceDesc());
        MutableComponent comp = Component.empty().append(timeComp).append(" - ").withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));

        //TODO: REPLACE
        comp = comp.append(countComp);

        return comp.append(blockSetCause.getInspectMessage(oldBlockComp, newBlockComp, sourceComp));
    }
}
