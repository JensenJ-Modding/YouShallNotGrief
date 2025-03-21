package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = DragonEggBlock.class, priority = 10100)
public class DragonEggBlockMixin {

    @Unique private Player youshallnotgrief$player;

    @Inject(method = "use", at = @At("HEAD"))
    private void youshallnotgrief$CapturePlayer1(
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Player player,
            InteractionHand interactionHand,
            BlockHitResult blockHitResult,
            CallbackInfoReturnable<InteractionResult> cir) {
        youshallnotgrief$player = player;
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void youshallnotgrief$CapturePlayer2(
            BlockState blockState, Level level, BlockPos blockPos, Player player, CallbackInfo ci) {
        youshallnotgrief$player = player;
    }

    @WrapOperation(
            method = "teleport",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public boolean youshallnotgrief$logDragonEggTeleportRemove(
            Level level, BlockPos pos, boolean b, Operation<Boolean> original) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if (ServerConfig.logDragonEggTeleportation.get()) {
                BlockUtils.addToDatabase(
                        pos,
                        level,
                        oldState,
                        Blocks.AIR.defaultBlockState(),
                        BlockSetCauses.USED,
                        youshallnotgrief$player,
                        "");
            }
        });
    }

    @WrapOperation(
            method = "teleport",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean youshallnotgrief$logDragonEggTeleportPlace(
            Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if (ServerConfig.logDragonEggTeleportation.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.LAND, youshallnotgrief$player, "");
            }
        });
    }
}
