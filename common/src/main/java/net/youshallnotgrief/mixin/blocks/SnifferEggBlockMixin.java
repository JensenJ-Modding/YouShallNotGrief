package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnifferEggBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = SnifferEggBlock.class, priority = 10100)
public class SnifferEggBlockMixin {

    @WrapOperation(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public boolean youshallnotgrief$logSnifferHatch(
            ServerLevel level, BlockPos pos, boolean b, Operation<Boolean> original) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if (ServerConfig.logMobHatching.get()) {
                BlockUtils.addToDatabase(
                        pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.HATCHED, null, "");
            }
        });
    }
}
