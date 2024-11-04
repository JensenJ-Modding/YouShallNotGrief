package net.youshallnotgrief.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {
    @Shadow @Final protected static Map<Block, BlockState> FLATTENABLES;

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public void youshallnotgrief$logPathFlattening(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState oldState = context.getLevel().getBlockState(context.getClickedPos());
        if(!FLATTENABLES.containsKey(oldState.getBlock())){
            return;
        }
        BlockState newState = FLATTENABLES.get(oldState.getBlock());
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(context.getClickedPos(), context.getLevel(), oldState, newState, BlockSetCauses.PAVED, context.getPlayer(), ""));
    }
}
