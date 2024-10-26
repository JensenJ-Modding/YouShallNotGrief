package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VineBlock.class)
public class VineBlockMixin {
    @WrapOperation(method="randomTick", at = @At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logVineGrowth(ServerLevel level, BlockPos blockPos, BlockState blockState, int i, Operation<Boolean> original) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(blockState, blockPos, level, BlockSetCauses.GROW, ""));
        return original.call(level, blockPos, blockState, i);
    }

    @Inject(method="updateShape", at = @At(value="RETURN"))
    private void youshallnotgrief$logVineBreak(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2, CallbackInfoReturnable<BlockState> cir){
        if(cir.getReturnValue() == Blocks.AIR.defaultBlockState()) {
            if(!levelAccessor.isClientSide()){
                if(levelAccessor instanceof Level level) {
                    DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(blockState, blockPos, level, BlockSetCauses.UNSUPPORTED, ""));
                }
            }
        }
    }
}
