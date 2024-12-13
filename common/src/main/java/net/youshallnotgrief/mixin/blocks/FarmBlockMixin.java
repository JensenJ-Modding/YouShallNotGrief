package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FarmBlock.class, priority = 10100)
public class FarmBlockMixin {
    @WrapOperation(method = "turnToDirt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean youshallnotgrief$logFarmChangeToDirt(Level level, BlockPos pos, BlockState state, Operation<Boolean> original, @Local(argsOnly = true) Entity entity) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(entity != null) {
                if (ServerConfig.logBlockTrampling.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.TRAMPLED, entity, "");
                }
            }else{
                if (ServerConfig.logDecay.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.DECAY, null, "");
                }
            }
        });
    }
}
