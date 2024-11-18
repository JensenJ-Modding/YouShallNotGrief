package net.youshallnotgrief.mixin.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.FeatureMixinHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;
import java.util.function.Function;

//TODO: Move mixin to TreeFeature setBlock check to ensure that only blocks which are actually placed are logged.
@Mixin(value = TrunkPlacer.class, priority = 10100)
public class TrunkPlacerMixin {

    //@Inject(method="placeLog(Lnet/minecraft/world/level/LevelSimulatedReader;Ljava/util/function/BiConsumer;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;Ljava/util/function/Function;)Z",
    //at = @At(value = "INVOKE", target="Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"))
    //private void youshallnotgrief$logTreeTrunkPlacement(LevelSimulatedReader levelSimulatedReader, BiConsumer<BlockPos, BlockState> biConsumer, RandomSource randomSource, BlockPos blockPos, TreeConfiguration treeConfiguration, Function<BlockState, BlockState> function, CallbackInfoReturnable<Boolean> cir){
    //    if(!FeatureMixinHolder.wasWorldgen){
    //        if(levelSimulatedReader instanceof Level level) {
    //            BlockState state = function.apply(treeConfiguration.trunkProvider.getState(randomSource, blockPos.immutable()));
    //            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos, level, state, BlockSetCauses.GROW, null, ""));
    //        }
    //    }
    //}
}
