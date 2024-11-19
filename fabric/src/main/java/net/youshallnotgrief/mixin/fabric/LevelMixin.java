package net.youshallnotgrief.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.util.ModdedBlockSetInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Level.class, priority = 10100)
public class LevelMixin {
    @Inject(at = @At("RETURN"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private void youshallnotgrief$logModdedSetBlockInteractions(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        //Only log if the block was actually set
        if (!cir.getReturnValue()){
            return;
        }

        Level level = (Level) (Object) this;
        ModdedBlockSetInteraction.logModdedSetBlockInteractions(blockPos, blockState, level);
    }
}
