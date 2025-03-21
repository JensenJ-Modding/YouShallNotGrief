package net.youshallnotgrief.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = AnvilMenu.class, priority = 10100)
public abstract class AnvilMenuMixin {

    @WrapOperation(
            method = "method_24922",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private static boolean youshallnotgrief$logAnvilBreak(
            Level level, BlockPos pos, boolean b, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if (ServerConfig.logAnvilUse.get()) {
                BlockUtils.addToDatabase(
                        pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.DAMAGED, player, "");
            }
        });
    }

    @WrapOperation(
            method = "method_24922",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logAnvilChange(
            Level level,
            BlockPos pos,
            BlockState state,
            int i,
            Operation<Boolean> original,
            @Local(argsOnly = true) Player player) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if (ServerConfig.logAnvilUse.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.DAMAGED, player, "");
            }
        });
    }

    @Inject(method = "method_24922", at = @At(value = "HEAD"))
    private static void youshallnotgrief$logAnvilUse(Player player, Level level, BlockPos pos, CallbackInfo ci) {
        if (level.isClientSide()) {
            return;
        }
        if (ServerConfig.logAnvilUse.get()) {
            BlockState state = level.getBlockState(pos);
            BlockUtils.addToDatabase(pos, level, state, state, BlockSetCauses.USED, player, "");
        }
    }
}
