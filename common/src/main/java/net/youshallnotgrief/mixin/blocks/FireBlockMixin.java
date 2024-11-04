package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target="Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public void youshallnotgrief$logFireExtinguishBlock(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource randomSource, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, null, blockState, BlockSetCauses.FIRE_EXTINGUISH, null, ""));
    }

    @Inject(method = "checkBurnOut", at = @At(value = "INVOKE", target="Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.AFTER))
    public void youshallnotgrief$logFireBreakBlock(Level level, BlockPos pos, int i, RandomSource randomSource, int j, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, Blocks.FIRE.defaultBlockState(), null, BlockSetCauses.FIRE, null, ""));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target="Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public void youshallnotgrief$logFireSpreadFire1(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource randomSource, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos.immutable(), level, null, blockState, BlockSetCauses.FIRE_SPREAD, null, ""));
    }

    @Inject(method = "checkBurnOut", at = @At(value = "INVOKE", target="Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public void youshallnotgrief$logFireSpreadFire2(Level level, BlockPos pos, int i, RandomSource randomSource, int j, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos.immutable(), level, null, Blocks.FIRE.defaultBlockState(), BlockSetCauses.FIRE_SPREAD, null, ""));
    }
}
