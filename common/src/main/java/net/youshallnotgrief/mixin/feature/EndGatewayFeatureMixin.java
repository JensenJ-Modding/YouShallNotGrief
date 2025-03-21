package net.youshallnotgrief.mixin.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndGatewayFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;

@Mixin(value = EndGatewayFeature.class, priority = 10100)
public class EndGatewayFeatureMixin {
    @WrapOperation(
            method = "place",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/levelgen/feature/EndGatewayFeature;setBlock(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void youshallnotgrief$logGatewayCreation(
            EndGatewayFeature instance,
            LevelWriter levelWriter,
            BlockPos pos,
            BlockState state,
            Operation<Void> original) {
        MixinDataHolder.wasLevelSetTracked = true;
        if (levelWriter instanceof Level level) {
            BlockState oldState = level.getBlockState(pos);
            if (!level.isClientSide()) {
                if (ServerConfig.logEndFight.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, null, "");
                }
            }
        }
        original.call(instance, levelWriter, pos, state);
    }
}
