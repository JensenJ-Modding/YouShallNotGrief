package net.youshallnotgrief.mixin.forge;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
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
    @Inject(method = "lambda$changeIntoState$3", at = @At("TAIL"))
    private static void youshallnotgrief$logHoeInteraction(BlockState blockState, UseOnContext context, CallbackInfo ci) {
        youshallnotgrief$log(context, blockState);
    }

    @Inject(method = "lambda$changeIntoStateAndDropItem$4", at = @At("TAIL"))
    private static void youshallnotgrief$logHoeInteraction(BlockState blockState, ItemLike itemLike, UseOnContext context, CallbackInfo ci) {
        youshallnotgrief$log(context, blockState);
    }

    @Unique
    private static void youshallnotgrief$log(UseOnContext context, BlockState state) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(context.getClickedPos(), context.getLevel(), null, state, BlockSetCauses.PLOUGHED, context.getPlayer(), ""));
    }
}
