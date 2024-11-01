package net.youshallnotgrief.event;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.utils.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.HitResult;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.DoorBlock.HALF;

public class BlockEvents {

    public static void registerEvents(){
        BlockEvent.BREAK.register((Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) -> {
            if(level.isClientSide()){
                return EventResult.pass();
            }

            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetAction.REMOVED, player, ""));
            handleTallBlockInteraction(state, pos, level, BlockSetAction.REMOVED, player);
            handleBedBlockInteraction(state, pos, level, BlockSetAction.REMOVED, player);

            return EventResult.pass();
        });

        BlockEvent.PLACE.register((Level level, BlockPos pos, BlockState state, @Nullable Entity placer) -> {
            if(level.isClientSide()){
                return EventResult.pass();
            }

            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromBlockState(state, pos, level, BlockSetAction.PLACED, placer, ""));
            handleTallBlockInteraction(state, pos, level, BlockSetAction.PLACED, placer);
            handleBedBlockInteraction(state, pos, level, BlockSetAction.PLACED, placer);

            return EventResult.pass();
        });

        BlockEvent.FALLING_LAND.register((Level level, BlockPos pos, BlockState fallState, BlockState landOn, FallingBlockEntity entity) ->
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCause(pos, level, BlockSetCauses.LAND, "")));

        InteractionEvent.FARMLAND_TRAMPLE.register((Level level, BlockPos pos, BlockState state, float distance, Entity entity) -> {
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetAction.TRAMPLED, entity, ""));
            return EventResult.pass();
        });

        PlayerEvent.FILL_BUCKET.register((Player player, Level level, ItemStack stack, @Nullable HitResult target) -> {
            if(target != null) {
                BlockPos pos = new BlockPos(new Vec3i((int) target.getLocation().x, (int) target.getLocation().y, (int) target.getLocation().z));
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetAction.BUCKETED, player, ""));
            }
            return CompoundEventResult.pass();
        });
    }

    private static void handleTallBlockInteraction(BlockState state, BlockPos pos, Level level, BlockSetAction action, Entity player){
        if(state.getBlock() instanceof DoorBlock || state.getBlock() instanceof DoublePlantBlock){
            DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
            if(doubleBlockHalf == DoubleBlockHalf.LOWER){
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromBlockState(state, pos.above(), level, action, player, ""));
            }else{
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromBlockState(state, pos.below(), level, action, player, ""));
            }
        }
    }

    private static void handleBedBlockInteraction(BlockState state, BlockPos pos, Level level, BlockSetAction action, Entity player){
        if(state.getBlock() instanceof BedBlock){
            Direction dir = BedBlock.getConnectedDirection(state);
            BlockPos otherPos = pos.relative(dir, 1);
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromBlockState(state, otherPos, level, action, player, ""));
        }
    }
}
