package net.youshallnotgrief.mixin.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;
import org.jetbrains.annotations.Nullable;

@Mixin(value = Explosion.class, priority = 10100)
public abstract class ExplosionMixin {

    @Final
    @Shadow
    private Entity source;

    @Shadow
    @Nullable public abstract LivingEntity getIndirectSourceEntity();

    @WrapOperation(
            method = "finalizeExplosion",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/block/state/BlockState;onBlockExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"))
    private void youshallnotgrief$logBlockExplosion(
            BlockState state, Level level, BlockPos pos, Explosion explosion, Operation<Void> original) {
        if (level.isClientSide()) {
            return;
        }
        BlockState oldState = level.getBlockState(pos);
        MixinDataHolder.wasLevelSetTracked = true;
        original.call(state, level, pos, explosion);

        if (ServerConfig.logExplosions.get()) {
            BlockUtils.addToDatabase(
                    pos,
                    level,
                    oldState,
                    state,
                    BlockSetCauses.EXPLOSION,
                    source,
                    EntityUtils.getSourceAndTargets(getIndirectSourceEntity()));
        }
    }

    @WrapOperation(
            method = "finalizeExplosion",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean youshallnotgrief$logBlockFireExplosion(
            Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if (ServerConfig.logExplosions.get()) {
                BlockUtils.addToDatabase(
                        pos,
                        level,
                        oldState,
                        state,
                        BlockSetCauses.PLACED,
                        source,
                        EntityUtils.getSourceAndTargets(getIndirectSourceEntity()));
            }
        });
    }
}
