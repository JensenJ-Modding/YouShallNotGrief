package net.youshallnotgrief.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.data.block.cause.BlockSetCause;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.data.block.cause.GrowCause;
import net.youshallnotgrief.data.block.cause.UnsupportedCause;
import net.youshallnotgrief.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static net.minecraft.world.level.block.DoorBlock.HALF;

public class BlockUtils {

    public static String getDimensionNameFromLevel(Level level){
        return level.dimension().location().toString();
    }

    public static String getBlockIDFromBlockState(BlockState state){
        ResourceLocation location = state.getBlock().arch$registryName();
        return location != null ? location.toString() : "";
    }

    public static BlockSetData makeBlockSetData(@NotNull BlockPos pos, @NotNull Level level, @Nullable BlockState oldState, @Nullable BlockState newState, @NotNull BlockSetCause cause, @Nullable Entity source, @NotNull String sourceDesc){
        if(oldState == null) { oldState = level.getBlockState(pos); }
        if(newState == null) { newState = level.getBlockState(pos); }
        String sourceText = "";
        if(source != null){
            sourceText = source.getName().getString();
        }
        return new BlockSetData(pos, getDimensionNameFromLevel(level), getCurrentTime(cause), getBlockIDFromBlockState(oldState), getBlockIDFromBlockState(newState), cause.getDatabaseTag(), sourceText, sourceDesc);
    }

    public static BlockSetData makeModdedBlockSetData(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockState oldState, @NotNull BlockState newState, @NotNull String modName, @NotNull String functionName){
        BlockSetCause cause = BlockSetCauses.MODDED;
        return new BlockSetData(pos, getDimensionNameFromLevel(level), getCurrentTime(cause), getBlockIDFromBlockState(oldState), getBlockIDFromBlockState(newState), cause.getDatabaseTag(), modName, functionName);
    }

    private static Timestamp getCurrentTime(@Nullable BlockSetCause cause){
        if(cause instanceof UnsupportedCause || cause instanceof GrowCause){
            //This is limited to seconds otherwise some actions will be logged multiple times due to some implementations of logging.
            //By setting the times to the same second, we are preventing the same action being put in the hashset, as they must be fully unique.
            return Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        }
        return Timestamp.valueOf(LocalDateTime.now());
    }

    //TODO: CALL
    private static void handleTallBlockInteraction(BlockState state, BlockPos pos, Level level, BlockSetCause cause, Entity player){
        if(state.getBlock() instanceof DoorBlock || state.getBlock() instanceof DoublePlantBlock){
            DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
            if(doubleBlockHalf == DoubleBlockHalf.LOWER){
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos.above(), level, null, state, cause, player, ""));
            }else{
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos.below(), level, null, state, cause, player, ""));
            }
        }
    }

    //TODO: CALL
    private static void handleBedBlockInteraction(BlockState state, BlockPos pos, Level level, BlockSetCause cause, Entity player){
        if(state.getBlock() instanceof BedBlock){
            Direction dir = BedBlock.getConnectedDirection(state);
            BlockPos otherPos = pos.relative(dir, 1);
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(otherPos, level, null, state, cause, player, ""));
        }
    }
}

