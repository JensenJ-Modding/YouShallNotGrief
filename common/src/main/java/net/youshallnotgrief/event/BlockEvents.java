package net.youshallnotgrief.event;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.utils.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.inspection.InspectionMode;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.BlockPositionMenuContext;
import net.youshallnotgrief.util.mixin.MixinDataHolder;
import org.jetbrains.annotations.Nullable;

public class BlockEvents {

    public static void registerEvents(){
        BlockEvent.BREAK.register((Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) -> {
            if(level.isClientSide()){
                return EventResult.pass();
            }

            if(InspectionMode.isPlayerInspecting(player)){
                return EventResult.interruptFalse();
            }

            BlockState newState = level.getBlockState(pos);
            MixinDataHolder.wasLevelSetTracked = true;
            if(ServerConfig.logBlockBreaking.get()) {
                BlockUtils.addToDatabase(pos, level, state, newState, BlockSetCauses.REMOVED, player, "");
            }
            return EventResult.pass();
        });

        //This event is used to track which inventory the player is currently accessing which is used in transaction logging
        InteractionEvent.RIGHT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) -> {
            Level level = player.level();
            if(level.isClientSide){
                return EventResult.pass();
            }

            if(hand == InteractionHand.OFF_HAND){
                return EventResult.pass();
            }

            if(InspectionMode.isPlayerInspecting(player)){
                return EventResult.interruptFalse();
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity == null){
                ((BlockPositionMenuContext) player).youshallnotgrief$setContainerPos(null);
                return EventResult.pass();
            }

            ((BlockPositionMenuContext) player).youshallnotgrief$setContainerPos(pos);
            if(ServerConfig.logContainerAccesses.get()){
                BlockState state = level.getBlockState(pos);
                BlockUtils.addToDatabase(pos, level, state, state, BlockSetCauses.ACCESSED, player, "");
            }
            return EventResult.pass();
        });

        //TODO: FIX
        PlayerEvent.FILL_BUCKET.register((Player player, Level level, ItemStack stack, @Nullable HitResult target) -> {
            if(level.isClientSide()){
                return CompoundEventResult.pass();
            }

            if(InspectionMode.isPlayerInspecting(player)){
                return CompoundEventResult.interruptFalse(null);
            }

            //if(target != null) {
            //    BlockPos pos = new BlockPos(new Vec3i((int) target.getLocation().x, (int) target.getLocation().y, (int) target.getLocation().z));
            //    BlockState oldState = level.getBlockState(pos);
            //    MixinDataHolder.wasLevelSetTracked = true;
            //    BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, player, "");
            //}
            return CompoundEventResult.pass();
        });
    }
}
