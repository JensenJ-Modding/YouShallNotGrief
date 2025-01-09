package net.youshallnotgrief.mixin.feature;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TreeFeature.class, priority = 10100)
public class TreeFeatureMixin {

    @Inject(method="place", at = @At(value="RETURN"))
    private void youshallnotgrief$resetFeatureFlag(FeaturePlaceContext<TreeConfiguration> featurePlaceContext, CallbackInfoReturnable<Boolean> cir) {
        MixinDataHolder.wasFeatureWorldgen = true;
    }

    @WrapOperation(method = "method_35364", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logTreeGrowthRoots(WorldGenLevel worldGenLevel, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(worldGenLevel, pos, state, i, original, oldState -> {
            if(MixinDataHolder.wasFeatureWorldgen){
                return;
            }
            if(worldGenLevel instanceof Level level){
                if(ServerConfig.logPlantGrowth.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
                }
            }
        });
    }

    @WrapOperation(method = "method_43162", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logTreeGrowthTrunks(WorldGenLevel worldGenLevel, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(worldGenLevel, pos, state, i, original, oldState -> {
            if(MixinDataHolder.wasFeatureWorldgen){
                return;
            }
            if(worldGenLevel instanceof Level level){
                if(ServerConfig.logPlantGrowth.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
                }
            }
        });
    }

    @WrapOperation(method = "method_49238", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logTreeGrowthDecorators(WorldGenLevel worldGenLevel, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(worldGenLevel, pos, state, i, original, oldState -> {
            if(MixinDataHolder.wasFeatureWorldgen){
                return;
            }
            if(worldGenLevel instanceof Level level){
                if(ServerConfig.logPlantGrowth.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
                }
            }
        });
    }
}
