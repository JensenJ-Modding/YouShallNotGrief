package net.youshallnotgrief.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.*;
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

    public static BlockSetData makeBlockSetData(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockSetAction action, @Nullable Entity source, @Nullable String sourceDesc){
        return new BlockSetData(new BlockSetPosData(pos, getDimensionNameFromLevel(level)),
                Timestamp.valueOf(LocalDateTime.now()),
                new BlockSetBlockData(getBlockNameFromBlockPos(level, pos), getBlockIDFromBlockPos(level, pos)),
                source != null ? action : BlockSetAction.SET,
                new BlockSetSourceData(source != null ? source.getName().getString() : "", sourceDesc != null ? sourceDesc : ""));
    }

    public static BlockSetData makeBlockSetData(@NotNull BlockPos pos, @NotNull Level level, @NotNull BlockSetAction action, @Nullable String source, @Nullable String sourceDesc){
        return new BlockSetData(new BlockSetPosData(pos, getDimensionNameFromLevel(level)),
                Timestamp.valueOf(LocalDateTime.now()),
                new BlockSetBlockData(getBlockNameFromBlockPos(level, pos), getBlockIDFromBlockPos(level, pos)),
                source != null ? action : BlockSetAction.SET,
                new BlockSetSourceData(source != null ? source : "", sourceDesc != null ? sourceDesc : ""));
    }
}

