package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BreakDoorGoal.class, priority = 10100)
public class BreakDoorGoalMixin {

    @WrapOperation(method="tick", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean youshallnotgrief$logDoorBreak(Level level, BlockPos pos, boolean b, Operation<Boolean> original){
        DoorInteractGoalAccessor accessor = (DoorInteractGoalAccessor) this;
        Mob mob = accessor.getMob();
        String sourceDesc = EntityUtils.getSourceAndTargets(mob);

        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if(ServerConfig.logMobDoorBreak.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, mob, sourceDesc);
                BlockUtils.addToDatabase(pos.below(), level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, mob, sourceDesc);
            }
        });
    }
}
