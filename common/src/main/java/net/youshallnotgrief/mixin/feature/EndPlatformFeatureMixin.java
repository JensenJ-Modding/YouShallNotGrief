package net.youshallnotgrief.mixin.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = EndPlatformFeature.class, priority = 10100)
public class EndPlatformFeatureMixin {

    @WrapOperation(
            method = "createEndPlatform",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean youshallnotgrief$logEndPlatformPlaceBlock(
            ServerLevelAccessor level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if (ServerConfig.logEndFight.get()) {
                BlockUtils.addToDatabase(pos, level.getLevel(), oldState, state, BlockSetCauses.PORTAL, null, "");
            }
        });
    }

    @WrapOperation(
            method = "createEndPlatform",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/ServerLevelAccessor;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private static boolean youshallnotgrief$logEndPlatformDestroyBlock(
            ServerLevelAccessor level, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original) {
        return BlockUtils.wrapLevelDestroyBlock((Level) level, pos, b, entity, original, oldState -> {
            if (ServerConfig.logEndFight.get()) {
                BlockUtils.addToDatabase(
                        pos, (Level) level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.PORTAL, null, "");
            }
        });
    }
}
