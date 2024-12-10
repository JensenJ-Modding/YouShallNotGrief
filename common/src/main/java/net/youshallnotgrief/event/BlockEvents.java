package net.youshallnotgrief.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.MixinDataHolder;
import org.jetbrains.annotations.Nullable;

public class BlockEvents {

    public static void registerEvents(){
        BlockEvent.BREAK.register((Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) -> {
            if(level.isClientSide()){
                return EventResult.pass();
            }

            BlockState newState = level.getBlockState(pos);
            MixinDataHolder.wasLevelSetTracked = true;
            if(ServerConfig.logBlockBreaking.get()) {
                BlockUtils.addToDatabase(pos, level, state, newState, BlockSetCauses.REMOVED, player, "");
            }
            return EventResult.pass();
        });

        InteractionEvent.FARMLAND_TRAMPLE.register((Level level, BlockPos pos, BlockState state, float distance, Entity entity) -> {
            BlockState newState = level.getBlockState(pos);
            MixinDataHolder.wasLevelSetTracked = true;
            if(ServerConfig.logBlockTrampling.get()) {
                BlockUtils.addToDatabase(pos, level, state, newState, BlockSetCauses.TRAMPLED, entity, "");
            }
            return EventResult.pass();
        });

        //TODO: FIX
        //PlayerEvent.FILL_BUCKET.register((Player player, Level level, ItemStack stack, @Nullable HitResult target) -> {
        //    if(level.isClientSide()){
        //        return CompoundEventResult.pass();
        //    }
        //    if(target != null) {
        //        BlockPos pos = new BlockPos(new Vec3i((int) target.getLocation().x, (int) target.getLocation().y, (int) target.getLocation().z));
        //        BlockState oldState = level.getBlockState(pos);
        //        MixinDataHolder.wasLevelSetTracked = true;
        //        BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, player, "");
        //    }
        //    return CompoundEventResult.pass();
        //});
    }
}
