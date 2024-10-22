package net.youshallnotgrief.mixin.fabric;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin {
    // These unmapped methods are lambda expressions used by the game for hoe uses
    @Inject(method = "method_36984", at = @At("TAIL"))
    private static void youshallnotgrief$logHoeInteraction(BlockState blockState, UseOnContext context, CallbackInfo ci) {
        youshallnotgrief$log(context);
    }

    @Inject(method = "method_36986", at = @At("TAIL"))
    private static void youshallnotgrief$logHoeInteraction(BlockState blockState, ItemLike itemLike, UseOnContext context, CallbackInfo ci) {
        youshallnotgrief$log(context);
    }

    @Unique
    private static void youshallnotgrief$log(UseOnContext context) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(context.getClickedPos(), context.getLevel(), BlockSetAction.PLOUGHED, context.getPlayer(), ""));
    }
}
