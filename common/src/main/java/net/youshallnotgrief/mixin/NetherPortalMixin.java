package net.youshallnotgrief.mixin;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILEXCEPTION;

@Mixin(PortalForcer.class)
public abstract class NetherPortalMixin {

    @Final
    @Shadow
    private ServerLevel level;

    @SuppressWarnings("all")
    @Inject(method = "createPortal", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;", shift = At.Shift.AFTER, by = 1), locals = CAPTURE_FAILEXCEPTION)
    public void youshallnotgrief$logPortalPlacement(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir, Direction direction, double d, BlockPos blockPos2, double e, BlockPos blockPos3, WorldBorder worldBorder, int i, BlockPos.MutableBlockPos mutableBlockPos) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(mutableBlockPos, level, BlockSetAction.PORTALED, "", ""));
    }
}
