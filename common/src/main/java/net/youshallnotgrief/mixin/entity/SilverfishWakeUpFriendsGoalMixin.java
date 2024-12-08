package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.Silverfish$SilverfishWakeUpFriendsGoal", priority = 10100)
public class SilverfishWakeUpFriendsGoalMixin {

    @Final
    @Shadow
    private Silverfish silverfish;

    @WrapOperation(method="tick", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean youshallnotgrief$logSilverfishHatchBlock(Level level, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original) {
        return BlockUtils.wrapLevelDestroyBlock(level, pos, b, entity, original, oldState -> {
            BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.HATCHED, silverfish, "");
        });
    }
}
