package net.youshallnotgrief.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//TODO: Unify into main mod if possible
@Mixin(value = AnvilMenu.class, priority = 10100)
public abstract class AnvilMenuMixin {

    @WrapOperation(method = "lambda$onTake$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private static boolean youshallnotgrief$logAnvilBreak(Level level, BlockPos pos, boolean b, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.DAMAGED, player, "");
        });
    }

    @WrapOperation(method = "lambda$onTake$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logAnvilChange(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.DAMAGED, player, "");
        });
    }
}


