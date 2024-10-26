package net.youshallnotgrief.mixin.blocks.feature;

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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FoliagePlacer.class)
public class FoliagePlacerMixin {
    @SuppressWarnings("all")
    @Inject(method="tryPlaceLeaf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer$FoliageSetter;set(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void youshallnotgrief$logFoliagePlacement(LevelSimulatedReader levelSimulatedReader, FoliagePlacer.FoliageSetter foliageSetter, RandomSource randomSource, TreeConfiguration treeConfiguration, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir, BlockState blockState){
        if(!FeatureMixinHolder.wasWorldgen){
            if(levelSimulatedReader instanceof Level level) {
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromNonPlayerCauseBlockState(blockState, blockPos.immutable(), level, BlockSetCauses.GROW, ""));
            }
        }
    }
}
