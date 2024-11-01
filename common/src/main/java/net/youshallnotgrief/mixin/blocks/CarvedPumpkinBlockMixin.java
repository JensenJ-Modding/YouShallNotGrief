package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @SuppressWarnings("all")
    @Inject(method="clearPatternBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void youshallnotgrief$logGolemCreation(Level level, BlockPattern.BlockPatternMatch blockPatternMatch, CallbackInfo ci, @Local BlockInWorld blockinWorld) {
        if(!level.isClientSide()) {
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCause(blockinWorld.getPos().immutable(), level, BlockSetCauses.GOLEM_CREATION, ""));
        }
    }
}
