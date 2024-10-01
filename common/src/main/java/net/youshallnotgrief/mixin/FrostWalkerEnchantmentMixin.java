package net.youshallnotgrief.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.BlockSetActions;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILEXCEPTION;

@Mixin(FrostWalkerEnchantment.class)
public abstract class FrostWalkerEnchantmentMixin {
    @SuppressWarnings("all")
    @Inject(method = "onEntityMoved", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", shift = At.Shift.AFTER, by = 1), locals = CAPTURE_FAILEXCEPTION)
    private static void youshallnotgrief$logFrostWalkerPlacement(LivingEntity livingEntity, Level level, BlockPos blockPos, int i, CallbackInfo ci,
                                                                 BlockState blockState, int j, BlockPos.MutableBlockPos mutableBlockPos, Iterator<BlockPos> var7, BlockPos blockPos2) {

        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos2, level, BlockSetActions.FROZE, livingEntity, "frost walker"));
    }
}
