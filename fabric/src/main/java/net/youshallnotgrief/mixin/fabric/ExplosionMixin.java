package net.youshallnotgrief.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.ExplosionUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Explosion.class, priority = 10100)
public abstract class ExplosionMixin {

    @Shadow
    public Entity source;

    @Shadow @Nullable
    public abstract LivingEntity getIndirectSourceEntity();

    @WrapOperation(method="finalizeExplosion", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logBlockExplosion(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logExplosions.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.EXPLOSION, source, ExplosionUtils.getSourceDescription(getIndirectSourceEntity()));
            }
        });
    }

    @WrapOperation(method="finalizeExplosion", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean youshallnotgrief$logBlockFireExplosion(Level level, BlockPos pos, BlockState state, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logExplosions.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PLACED, source, ExplosionUtils.getSourceDescription(getIndirectSourceEntity()));
            }
        });
    }
}
