package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCause;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractCandleBlock.class, priority = 10100)
public class AbstractCandleBlockMixin {

    @WrapOperation(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AbstractCandleBlock;setLit(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V"))
    private void youshallnotgrief$logCandleLit(LevelAccessor levelAccessor, BlockState state, BlockPos pos, boolean bl, Operation<Void> original, @Local(argsOnly = true) Projectile projectile) {
        if(levelAccessor instanceof Level level) {
            if(ServerConfig.logProjectileLightBlock.get()) {
                youshallnotgrief$logCandleLighting(level, state, pos, bl, projectile.getOwner());
            }
        }
        MixinDataHolder.wasLevelSetTracked = true;
        original.call(levelAccessor, state, pos, bl);
    }

    @WrapOperation(method = "extinguish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AbstractCandleBlock;setLit(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V"))
    private static void youshallnotgrief$logCandleExtinguish(LevelAccessor levelAccessor, BlockState state, BlockPos pos, boolean bl, Operation<Void> original, @Local(argsOnly = true) Player player) {
        if(levelAccessor instanceof Level level) {
            if(ServerConfig.logExtinguish.get()) {
                youshallnotgrief$logCandleLighting(level, state, pos, bl, player);
            }
        }
        MixinDataHolder.wasLevelSetTracked = true;
        original.call(levelAccessor, state, pos, bl);
    }

    @Unique
    private static void youshallnotgrief$logCandleLighting(Level level, BlockState state, BlockPos pos, boolean bl, Entity entity){
        if(level.isClientSide()){
            return;
        }
        BlockState oldState = level.getBlockState(pos);
        BlockSetCause cause = bl ? BlockSetCauses.LIT : BlockSetCauses.EXTINGUISH;
        BlockUtils.addToDatabase(pos, level, oldState, state, cause, entity, "");
    }
}
