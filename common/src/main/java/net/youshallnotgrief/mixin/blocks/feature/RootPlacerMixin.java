package net.youshallnotgrief.mixin.blocks.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BiConsumer;

@Mixin(RootPlacer.class)
public abstract class RootPlacerMixin {

    @Final
    @Shadow
    protected BlockStateProvider rootProvider;
    @Shadow protected abstract BlockState getPotentiallyWaterloggedState(LevelSimulatedReader arg, BlockPos arg2, BlockState arg3);

    @Inject(method="placeRoot",
            at = @At(value = "INVOKE", target="Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0))
    private void youshallnotgrief$logRootPlacement(LevelSimulatedReader levelSimulatedReader, BiConsumer<BlockPos, BlockState> biConsumer, RandomSource randomSource, BlockPos blockPos, TreeConfiguration treeConfiguration, CallbackInfo ci){
        if(!FeatureMixinHolder.wasWorldgen){
            if(levelSimulatedReader instanceof Level level) {
                BlockState state = getPotentiallyWaterloggedState(levelSimulatedReader, blockPos, this.rootProvider.getState(randomSource, blockPos));
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(state, blockPos, level, BlockSetCauses.GROW, ""));
            }
        }
    }

    @SuppressWarnings("all")
    @Inject(method="placeRoot",
            at = @At(value = "INVOKE", target="Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void youshallnotgrief$logRootDecoPlacement(LevelSimulatedReader levelSimulatedReader, BiConsumer<BlockPos, BlockState> biConsumer, RandomSource randomSource, BlockPos blockPos, TreeConfiguration treeConfiguration, CallbackInfo ci, AboveRootPlacement aboveRootPlacement, BlockPos blockPos2){
        if(!FeatureMixinHolder.wasWorldgen){
            if(levelSimulatedReader instanceof Level level) {
                BlockState state = getPotentiallyWaterloggedState(levelSimulatedReader, blockPos2, aboveRootPlacement.aboveRootProvider().getState(randomSource, blockPos2));
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(state, blockPos2, level, BlockSetCauses.GROW, ""));
            }
        }
    }
}
