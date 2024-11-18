package net.youshallnotgrief.mixin.feature;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.FeatureMixinHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: Move mixin to TreeFeature setBlock check to ensure that only blocks which are actually placed are logged.
@Mixin(value = FoliagePlacer.class, priority = 10100)
public class FoliagePlacerMixin {

    //@Inject(method = "tryPlaceLeaf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer$FoliageSetter;set(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    //private static void youshallnotgrief$logFoliagePlacement(LevelSimulatedReader levelSimulatedReader, FoliagePlacer.FoliageSetter foliageSetter, RandomSource randomSource, TreeConfiguration treeConfiguration, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir, @Local BlockState blockState){
    //    if(!FeatureMixinHolder.wasWorldgen){
    //        if(levelSimulatedReader instanceof Level level) {
    //            BlockUtils.addToDatabase(blockPos, level, level.getBlockState(blockPos), blockState, BlockSetCauses.GROW, null, "");
    //        }
    //    }
    //}
}
