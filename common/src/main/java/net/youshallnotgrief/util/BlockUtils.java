package net.youshallnotgrief.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.*;
import net.youshallnotgrief.data.block.cause.BlockSetCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class BlockUtils {

    public static String getBlockNameFromBlockPos(Level level, BlockPos pos){
        return getBlockNameFromBlockState(level.getBlockState(pos));
    }

    public static String getBlockIDFromBlockPos(Level level, BlockPos pos){
        return getBlockIDFromBlockState(level.getBlockState(pos));
    }

    public static String getDimensionNameFromLevel(Level level){
        return level.dimension().location().toString();
    }

    public static String getBlockNameFromBlockState(BlockState state){
        return state.getBlock().getName().getString();
    }

    public static String getBlockIDFromBlockState(BlockState state){
        ResourceLocation location = state.getBlock().arch$registryName();
        return location != null ? location.toString() : "";
    }

    //To be used in most cases.
    public static BlockSetData makeBlockSetData(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockSetAction action, @Nullable Entity source, @Nullable String sourceDesc){
        return new BlockSetData(new BlockSetPosData(pos, getDimensionNameFromLevel(level)),
                Timestamp.valueOf(LocalDateTime.now()),
                new BlockSetBlockData(getBlockIDFromBlockPos(level, pos), getBlockNameFromBlockPos(level, pos)),
                source != null ? action : BlockSetAction.SET,
                new BlockSetSourceData(source != null ? source.getName().getString() : "", sourceDesc != null ? sourceDesc : ""));
    }

    //To be used when we want to capture a blockstate which won't be in the level at the time the event is called.
    public static BlockSetData makeBlockSetDataFromBlockState(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level level, @NotNull BlockSetAction action, @Nullable Entity source, @Nullable String sourceDesc){
        return new BlockSetData(new BlockSetPosData(pos, getDimensionNameFromLevel(level)),
                Timestamp.valueOf(LocalDateTime.now()),
                new BlockSetBlockData(getBlockIDFromBlockState(state), getBlockNameFromBlockState(state)),
                source != null ? action : BlockSetAction.SET,
                new BlockSetSourceData(source != null ? source.getName().getString() : "", sourceDesc != null ? sourceDesc : ""));
    }

    //To be used when the cause is a non-player vanilla interaction.
    public static BlockSetData makeBlockSetDataFromNonPlayerCause(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockSetCause cause, @Nullable String details){
        return new BlockSetData(new BlockSetPosData(pos, getDimensionNameFromLevel(level)),
                Timestamp.valueOf(LocalDateTime.now()),
                new BlockSetBlockData(getBlockIDFromBlockPos(level, pos), getBlockNameFromBlockPos(level, pos)),
                BlockSetAction.SET,
                new BlockSetSourceData(cause.getDatabaseTag(), details != null ? details : ""));
    }

    //To be used for modded interactions
    public static BlockSetData makeBlockSetDataFromModdedInteraction(@NotNull BlockPos pos, @NotNull Level level, @NotNull String modID, @NotNull String modFunction){
        return new BlockSetData(new BlockSetPosData(pos, getDimensionNameFromLevel(level)),
                Timestamp.valueOf(LocalDateTime.now()),
                new BlockSetBlockData(getBlockIDFromBlockPos(level, pos), getBlockNameFromBlockPos(level, pos)),
                BlockSetAction.SET,
                new BlockSetSourceData(modID, modFunction));
    }
}

