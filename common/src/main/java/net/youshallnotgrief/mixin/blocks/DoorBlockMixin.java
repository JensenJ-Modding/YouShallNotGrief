package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.minecraft.world.level.block.DoorBlock.HALF;

@Mixin(DoorBlock.class)
public class DoorBlockMixin {

    @Inject(method="setOpen", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void youshallnotgrief$logDoorOpen(Entity entity, Level level, BlockState blockState, BlockPos blockPos, boolean bl, CallbackInfo ci){
        if(!level.isClientSide()){
            BlockPos blockPos2 = blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? blockPos.above() : blockPos.below();
            BlockSetAction action = bl ? BlockSetAction.OPENED : BlockSetAction.CLOSED;

            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos, level, action, entity, ""));
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos2, level, action, entity, ""));
        }
    }
}
