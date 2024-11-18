package net.youshallnotgrief.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 10100)
public class BlockBehaviourMixin {

    //TODO: redo this so that it only happens when the block is actually removed, as some blocks will check if they can survive multiple times, and may not even act on it, find root cause by generating call stack
    @WrapOperation(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean youshallnotgrief$logNoSupport(Block block, BlockState blockState, LevelReader levelReader, BlockPos blockPos, Operation<Boolean> original) {
        boolean originalResult = original.call(block, blockState, levelReader, blockPos);

        if(!levelReader.isClientSide()){
            if (!originalResult) {
                if(levelReader instanceof Level level){
                    BlockUtils.addToDatabase(blockPos, level, blockState, Blocks.AIR.defaultBlockState(), BlockSetCauses.UNSUPPORTED, null, "");
                }
            }
        }

        return originalResult;
    }
}
