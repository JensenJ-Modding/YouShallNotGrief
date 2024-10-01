package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.BlockSetActions;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScaffoldingBlock.class)
public abstract class ScaffoldingBlockMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.AFTER))
    public void youshallnotgrief$logScaffoldGravityBreak(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetActions.FELL, "", ""));
    }

}
