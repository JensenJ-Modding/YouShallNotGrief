package net.youshallnotgrief.mixin.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;

@Mixin(targets = "net.minecraft.world.level.levelgen.feature.TreeFeature$1", priority = 10100)
public class TreeFeatureFoliageSetterMixin {

    @WrapOperation(
            method = "set",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logTreeGrowthFoliage(
            WorldGenLevel worldGenLevel, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlock(worldGenLevel, pos, state, i, original, oldState -> {
            if (MixinDataHolder.wasFeatureWorldgen) {
                return;
            }
            if (worldGenLevel instanceof Level level) {
                if (ServerConfig.logPlantGrowth.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
                }
            }
        });
    }
}
