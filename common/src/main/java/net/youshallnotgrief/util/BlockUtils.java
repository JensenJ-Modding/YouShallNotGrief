package net.youshallnotgrief.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.data.block.cause.BlockSetCause;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class BlockUtils {

    private static final Registrar<Block> BLOCKS_REGISTRY = YouShallNotGriefMod.REGISTRY_MANAGER.get().get(Registries.BLOCK);

    public static String getDimensionNameFromLevel(Level level){
        return level.dimension().location().toString();
    }

    public static String getBlockIDFromBlockState(BlockState state){
        ResourceLocation location = state.getBlock().arch$registryName();
        return location != null ? location.toString() : "";
    }

    public static String getBlockNameFromBlockState(BlockState state){
        return state.getBlock().getName().getString();
    }

    public static Block getBlockFromString(String resourceLocation){
        return BLOCKS_REGISTRY.get(new ResourceLocation(resourceLocation));
    }

    //Should be called after a call to level.setBlock has been made.
    public static void addToDatabase(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockState oldState, @NotNull BlockState newState, @NotNull BlockSetCause cause, @Nullable Entity source, @NotNull String sourceDesc){
        String sourceText = "";
        if(source != null){
            sourceText = source.getName().getString();
        }
        BlockSetData data = new BlockSetData(pos.immutable(), getDimensionNameFromLevel(level), Timestamp.valueOf(LocalDateTime.now()),
                getBlockIDFromBlockState(oldState), getBlockIDFromBlockState(newState), cause.getDatabaseTag(), sourceText, sourceDesc);
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(data);
    }

    //Should be called after a call to level.setBlock has been made
    public static void addToDatabase(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockState oldState, @NotNull BlockState newState, @NotNull String modName, @NotNull String functionName){
        BlockSetCause cause = BlockSetCauses.MODDED;
        BlockSetData data =  new BlockSetData(pos.immutable(), getDimensionNameFromLevel(level), Timestamp.valueOf(LocalDateTime.now()), getBlockIDFromBlockState(oldState), getBlockIDFromBlockState(newState), cause.getDatabaseTag(), modName, functionName);
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(data);
    }

    //Used by mixins to ensure that setBlock was actually successful before recording changes.
    public static boolean wrapLevelSetBlock(LevelAccessor level, BlockPos pos, BlockState state, int i, Operation<Boolean> originalSet, Consumer<BlockState> callback)
    {
        MixinDataHolder.wasLevelSetTracked = true;
        BlockState oldState = level.getBlockState(pos);
        boolean wasSet = originalSet.call(level, pos, state, i);
        if(wasSet){
            if(!level.isClientSide()) {
                callback.accept(oldState);
            }
        }
        return wasSet;
    }

    //Used by mixins to ensure that setBlockAndUpdate was actually successful before recording changes.
    public static boolean wrapLevelSetBlockAndUpdate(LevelAccessor level, BlockPos pos, BlockState state, Operation<Boolean> originalSet, Consumer<BlockState> callback)
    {
        MixinDataHolder.wasLevelSetTracked = true;
        BlockState oldState = level.getBlockState(pos);
        boolean wasSet = originalSet.call(level, pos, state);
        if(wasSet){
            if(!level.isClientSide()) {
                callback.accept(oldState);
            }
        }
        return wasSet;
    }

    //Used by mixins to ensure that removeBlock was actually successful before recording changes.
    public static boolean wrapLevelRemoveBlock(Level level, BlockPos pos, boolean b, Operation<Boolean> originalRemove, Consumer<BlockState> callback)
    {
        MixinDataHolder.wasLevelSetTracked = true;
        BlockState oldState = level.getBlockState(pos);
        boolean wasSet = originalRemove.call(level, pos, b);
        if(wasSet){
            if(!level.isClientSide()) {
                callback.accept(oldState);
            }
        }
        return wasSet;
    }

    public static boolean wrapLevelDestroyBlock(Level level, BlockPos pos, boolean b, Entity entity, Operation<Boolean> originalRemove, Consumer<BlockState> callback)
    {
        MixinDataHolder.wasLevelSetTracked = true;
        BlockState oldState = level.getBlockState(pos);
        boolean wasSet = originalRemove.call(level, pos, b, entity);
        if(wasSet){
            if(!level.isClientSide()) {
                callback.accept(oldState);
            }
        }
        return wasSet;
    }

    //TODO: CALL/REFACTOR
    //private static void handleTallBlockInteraction(BlockState state, BlockPos pos, Level level, BlockSetCause cause, Entity player){
    //    if(state.getBlock() instanceof DoorBlock || state.getBlock() instanceof DoublePlantBlock){
    //        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
    //        if(doubleBlockHalf == DoubleBlockHalf.LOWER){
    //            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos.above(), level, null, state, cause, player, ""));
    //        }else{
    //            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos.below(), level, null, state, cause, player, ""));
    //        }
    //    }
    //}

    //TODO: CALL/REFACTOR
    //private static void handleBedBlockInteraction(BlockState state, BlockPos pos, Level level, BlockSetCause cause, Entity player){
    //    if(state.getBlock() instanceof BedBlock){
    //        Direction dir = BedBlock.getConnectedDirection(state);
    //        BlockPos otherPos = pos.relative(dir, 1);
    //        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(otherPos, level, state, null, cause, player, ""));
    //    }
    //}
}

