package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FireBlock.class, priority = 10100)
public abstract class FireBlockMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public boolean youshallnotgrief$logFireExtinguish(ServerLevel level, BlockPos pos, boolean b, Operation<Boolean> original) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if(ServerConfig.logFire.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.EXTINGUISH, null, "");
            }
        });
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    public boolean youshallnotgrief$logFireSpreadFire(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logFire.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.SPREAD, null, "");
            }
        });
    }
}
