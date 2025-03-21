package net.youshallnotgrief.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;

@Mixin(value = EnderDragon.class, priority = 10100)
public class EnderDragonMixin {

    @WrapOperation(
            method = "checkWalls",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean youshallnotgrief$logEnderDragonBreakBlock(
            Level level, BlockPos pos, boolean b, Operation<Boolean> original) {
        EnderDragon dragon = (EnderDragon) (Object) this;
        String sourceDesc = EntityUtils.getSourceAndTargets(dragon);
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if (ServerConfig.logEnderDragonGriefing.get()) {
                BlockUtils.addToDatabase(
                        pos,
                        level,
                        oldState,
                        Blocks.AIR.defaultBlockState(),
                        BlockSetCauses.REMOVED,
                        dragon,
                        sourceDesc);
            }
        });
    }
}
