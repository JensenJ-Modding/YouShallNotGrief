package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = CarvedPumpkinBlock.class, priority = 10100)
public class CarvedPumpkinBlockMixin {

    @WrapOperation(
            method = "clearPatternBlocks",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logGolemCreation(
            Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if (ServerConfig.logGolemCreation.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GOLEM_CREATION, null, "");
            }
        });
    }
}
