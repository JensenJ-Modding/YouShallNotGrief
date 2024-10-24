package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RootedDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RootedDirtBlock.class)
public abstract class RootedDirtBlockMixin {
    @Inject(method = "performBonemeal", at = @At("TAIL"))
    public void youshallnotgrief$logHangingRootsGrowth(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCause(pos, level, BlockSetCauses.GROW, ""));
    }
}
