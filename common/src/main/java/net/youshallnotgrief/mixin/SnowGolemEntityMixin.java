package net.youshallnotgrief.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.BlockSetActions;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILEXCEPTION;

@Mixin(SnowGolem.class)
public abstract class SnowGolemEntityMixin {
    @SuppressWarnings("all")
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", shift = At.Shift.AFTER, by = 1), locals = CAPTURE_FAILEXCEPTION)
    public void youshallnotgrief$logSnowGolemSnow(CallbackInfo ci, BlockState blockState, int i, int j, int k, int l, BlockPos blockPos) {
        SnowGolem golem = ((SnowGolem) (Object) this);
        Level level = golem.level();
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos, level, BlockSetActions.SNOW_GOLEM, golem, ""));
    }
}
