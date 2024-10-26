package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {
    @Inject(method = "growBamboo", at = @At(value = "TAIL"))
    public void youshallnotgrief$logBambooGrow(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource, int i, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCause(blockPos.above(), level, BlockSetCauses.GROW, ""));
    }
}