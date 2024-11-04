package net.youshallnotgrief.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {

    @SuppressWarnings("all")
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift= At.Shift.AFTER, by = 1), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void youshallnotgrief$logAxeUsage(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal=3) Optional<BlockState> optional4) {

        BlockState newState = optional4.orElse(Blocks.AIR.defaultBlockState());
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(context.getClickedPos(), context.getLevel(), null, newState, BlockSetCauses.SCRAPED, context.getPlayer(), ""));
    }
}
