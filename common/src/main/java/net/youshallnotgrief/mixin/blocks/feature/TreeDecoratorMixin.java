package net.youshallnotgrief.mixin.blocks.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.FeatureMixinHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TreeDecorator.Context.class)
public class TreeDecoratorMixin {
    @Final
    @Shadow
    private LevelSimulatedReader level;

    @Inject(method="setBlock",
            at = @At(value = "INVOKE", target="Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void youshallnotgrief$logTreeDecorationPlacement(BlockPos blockPos, BlockState blockState, CallbackInfo ci){
        if(!FeatureMixinHolder.wasWorldgen){
            if(level instanceof Level) {
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(blockState, blockPos.immutable(), (Level) level, BlockSetCauses.GROW, ""));
            }
        }
    }
}
