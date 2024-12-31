package net.youshallnotgrief.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.data.block.cause.BlockSetCause;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.mixin.MixinDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.BED_PART;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;

public class BlockUtils {

    private static final Registrar<Block> BLOCKS_REGISTRY = YouShallNotGriefMod.REGISTRY_MANAGER.get().get(Registries.BLOCK);
    private static boolean isPropagating = false;

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
        addToDatabaseRaw(pos.immutable(), level, oldState, newState, cause, sourceText, sourceDesc);
    }

    //Should be called after a call to level.setBlock has been made
    public static void addToDatabaseRaw(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockState oldState, @NotNull BlockState newState, @NotNull BlockSetCause cause, @NotNull String source, @NotNull String sourceDesc){
        propagateDatabaseInteraction(pos.immutable(), level, oldState, newState, cause, source, sourceDesc);
        BlockSetData data = new BlockSetData(pos.immutable(), MiscUtils.getDimensionIDFromLevel(level), Timestamp.valueOf(LocalDateTime.now()), getBlockIDFromBlockState(oldState), getBlockIDFromBlockState(newState), cause.getDatabaseTag(), source, sourceDesc);
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

    //This function is used to copy the details of one block interaction into another blockPos, useful for blocks which lazily update state on next block update, such as doors, plants and beds
    private static void propagateDatabaseInteraction(BlockPos pos, Level level, BlockState oldState, BlockState newState, BlockSetCause cause, String source, String sourceDesc){
        if(isPropagating){
            return;
        }
        //This runs after the main block interaction has been made, so oldState and newState may not be state accurate, but will be block accurate
        isPropagating = true;
        handleTallBlockInteraction(level, pos, oldState, newState, cause, source, sourceDesc);
        handleBedBlockInteraction(level, pos, oldState, newState, cause, source, sourceDesc);
        isPropagating = false;
    }

    //Helper method to work out which block in an interaction has a desired property, if any
    private static BlockState isValidState(BlockState oldState, BlockState newState, EnumProperty<?> property){
        if(oldState.hasProperty(property)){
            return oldState;
        }
        if(newState.hasProperty(property)){
            return newState;
        }
        return null;
    }

    private static void handleTallBlockInteraction(Level level, BlockPos pos, BlockState oldState, BlockState newState, BlockSetCause cause, String source, String sourceDesc){
        BlockState state = isValidState(oldState, newState, DOUBLE_BLOCK_HALF);
        if(state == null){
            return;
        }

        DoubleBlockHalf doubleBlockHalf = state.getValue(DOUBLE_BLOCK_HALF);
        if(doubleBlockHalf == DoubleBlockHalf.LOWER){
            addToDatabaseRaw(pos.above(), level, oldState, newState, cause, source, sourceDesc);
        }else{
            addToDatabaseRaw(pos.below(), level, oldState, newState, cause, source, sourceDesc);
        }
    }

    private static void handleBedBlockInteraction(Level level, BlockPos pos, BlockState oldState, BlockState newState, BlockSetCause cause, String source, String sourceDesc){
        BlockState state = isValidState(oldState, newState, BED_PART);
        if(state == null){
            return;
        }

        Direction dir = BedBlock.getConnectedDirection(state);
        BlockPos otherPos = pos.relative(dir, 1);
        addToDatabaseRaw(otherPos, level, oldState, newState, cause, source, sourceDesc);
    }
}