package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILEXCEPTION;

@Mixin(StemBlock.class)
public class StemBlockMixin {
    @SuppressWarnings("all")
    @Inject(method = "randomTick", at = @At(value = "INVOKE", target="Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=0), locals = CAPTURE_FAILEXCEPTION)
    public void youshallnotgrief$logStemBlockGrow(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci, float f, int i, Direction direction, BlockPos blockPos2) {
        StemBlock block = (StemBlock) (Object) this;
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(block.getFruit().defaultBlockState(), blockPos2, serverLevel, BlockSetCauses.GROW, ""));
    }
}

