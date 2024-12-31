package net.youshallnotgrief.mixin.blocks.plant;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.MixinDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChorusFlowerBlock.class, priority = 10100)
public class ChorusFlowerBlockMixin {

    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logChorusRandomTick(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logPlantGrowth.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
            }
        });
    }

    @WrapOperation(method = "placeGrownFlower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logChorusGrowFlower(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logPlantGrowth.get()) {
                //Don't log blockstate changes
                if(oldState.getBlock() == state.getBlock()){
                    return;
                }
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
            }
        });
    }

    @WrapOperation(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean youshallnotgrief$logProjectileHit(Level level, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original){
        return BlockUtils.wrapLevelDestroyBlock(level, pos, b, entity, original, oldState -> {
            if(ServerConfig.logBlockBreaking.get()) {
                if(entity instanceof Projectile projectile) {
                    BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, projectile.getOwner(), "");
                }
            }
        });
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private void youshallnotgrief$logChorusTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci){
        MixinDataHolder.wasLevelSetTracked = true;
    }

    @Inject(method = "placeDeadFlower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void youshallnotgrief$logChorusTick(Level level, BlockPos blockPos, CallbackInfo ci){
        MixinDataHolder.wasLevelSetTracked = true;
    }
}
