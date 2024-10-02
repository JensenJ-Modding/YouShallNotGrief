package net.youshallnotgrief.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILEXCEPTION;

@Mixin(Ravager.class)
public abstract class RavagerEntityMixin {
    @SuppressWarnings("all")
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.AFTER), locals = CAPTURE_FAILEXCEPTION)
    public void youshallnotgrief$logRavagerBreakingLeaves(CallbackInfo ci, boolean bl, AABB aABB, Iterator<BlockPos> var3, BlockPos blockPos, BlockState blockState) {
        Level level = ((Ravager) (Object) this).level();
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos, level, BlockSetAction.RAVAGER, "", ""));
    }
}
