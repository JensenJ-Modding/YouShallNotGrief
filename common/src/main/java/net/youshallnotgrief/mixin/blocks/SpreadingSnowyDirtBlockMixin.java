package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = SpreadingSnowyDirtBlock.class, priority = 10100)
public class SpreadingSnowyDirtBlockMixin {

    @WrapOperation(
            method = "randomTick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logGrassSpreading(
            ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if (ServerConfig.logGrassSpread.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, null, "");
            }
        });
    }
}
