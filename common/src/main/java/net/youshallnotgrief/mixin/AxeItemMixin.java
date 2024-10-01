package net.youshallnotgrief.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.youshallnotgrief.data.BlockSetActions;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {

    //Stripping logs
    //Scraping
    //Wax removal

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift= At.Shift.AFTER, by = 1))
    public void youshallnotgrief$logAxeUsage(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(context.getClickedPos(), context.getLevel(), BlockSetActions.SCRAPED, context.getPlayer(), ""));
    }
}
