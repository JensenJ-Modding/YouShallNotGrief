package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 10100)
public class BlockBehaviourMixin {

    @WrapOperation(
            method = "canSurvive",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/block/Block;canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean youshallnotgrief$logNoSupport(
            Block block,
            BlockState blockState,
            LevelReader levelReader,
            BlockPos blockPos,
            Operation<Boolean> original) {
        boolean originalResult = original.call(block, blockState, levelReader, blockPos);

        if (!levelReader.isClientSide()) {
            if (!originalResult) {
                if (levelReader instanceof ServerLevel level) {
                    if (youshallnotgrief$shouldRecordNoSupport(level, blockPos)) {
                        MixinDataHolder.wasLevelSetTracked = true;
                        if (ServerConfig.logUnsupportedBlocks.get()) {
                            BlockUtils.addToDatabase(
                                    blockPos,
                                    level,
                                    blockState,
                                    Blocks.AIR.defaultBlockState(),
                                    BlockSetCauses.UNSUPPORTED,
                                    null,
                                    "");
                        }
                    }
                }
            }
        }

        return originalResult;
    }

    @Unique private int youshallnotgrief$lastTick = -1;

    @Unique private BlockPos youshallnotgrief$lastBlockPos = null;

    @Unique private ResourceKey<Level> youshallnotgrief$lastDimension = null;

    // If it has been more than 5 ticks since last recording, or the block position and dimension are not the same
    // Needed to prevent duplicate recordings of some entries
    @Unique private boolean youshallnotgrief$shouldRecordNoSupport(ServerLevel level, BlockPos pos) {
        int tickCount = level.getServer().getTickCount();
        if (tickCount >= youshallnotgrief$lastTick + 5
                || !(pos == youshallnotgrief$lastBlockPos && level.dimension() == youshallnotgrief$lastDimension)) {
            youshallnotgrief$lastTick = tickCount;
            youshallnotgrief$lastBlockPos = pos;
            youshallnotgrief$lastDimension = level.dimension();
            return true;
        }
        return false;
    }
}
