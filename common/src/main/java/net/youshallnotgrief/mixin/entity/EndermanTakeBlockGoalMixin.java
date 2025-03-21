package net.youshallnotgrief.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanTakeBlockGoal", priority = 10100)
public class EndermanTakeBlockGoalMixin {

    @Final
    @Shadow
    private EnderMan enderman;

    @WrapOperation(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean youshallnotgrief$logEndermanTakeBlock(
            Level level, BlockPos pos, boolean b, Operation<Boolean> original) {
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if (ServerConfig.logEndermanGriefing.get()) {
                BlockUtils.addToDatabase(
                        pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, enderman, "");
            }
        });
    }
}
