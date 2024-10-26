package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviourMixin {

    @WrapOperation(
            method = "canSurvive",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean youshallnotgrief$logNoSupport(Block block, BlockState blockState, LevelReader levelReader, BlockPos blockPos, Operation<Boolean> original) {
        @SuppressWarnings("deprecation")
        boolean originalResult = block.canSurvive(blockState, levelReader, blockPos);

        if(!levelReader.isClientSide()){
            if (!originalResult) {
                if(levelReader instanceof Level level){
                    DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(blockState, blockPos.immutable(), level, BlockSetCauses.UNSUPPORTED, ""));
                }
            }
        }

        return originalResult;
    }
}
