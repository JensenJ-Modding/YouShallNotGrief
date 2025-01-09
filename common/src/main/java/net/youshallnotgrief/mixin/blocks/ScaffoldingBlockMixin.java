package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ScaffoldingBlock.class, priority = 10100)
public abstract class ScaffoldingBlockMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public boolean youshallnotgrief$logScaffoldUnsupportedBreak(ServerLevel level, BlockPos pos, boolean b, Operation<Boolean> original) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if(ServerConfig.logUnsupportedBlocks.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.UNSUPPORTED, null, "");
            }
        });
    }

}
