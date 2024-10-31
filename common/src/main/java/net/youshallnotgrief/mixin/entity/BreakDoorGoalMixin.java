package net.youshallnotgrief.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakDoorGoal.class)
public class BreakDoorGoalMixin {
    @Inject(method="tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private void youshallnotgrief$logDoorBreak(CallbackInfo ci){
        DoorInteractGoalAccessor accessor = (DoorInteractGoalAccessor) this;
        BlockPos doorPos = accessor.getDoorPos();
        Mob mob = accessor.getMob();
        Level level = mob.level();

        if(!level.isClientSide()){
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(doorPos, level, BlockSetAction.REMOVED, mob, ""));
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(doorPos.below(), level, BlockSetAction.REMOVED, mob, ""));
        }
    }
}
