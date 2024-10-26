package net.youshallnotgrief.mixin.blocks.feature;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.youshallnotgrief.util.FeatureMixinHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

    @Inject(method="place", at = @At(value="RETURN"))
    private void youshallnotgrief$resetFeatureFlag(FeaturePlaceContext<TreeConfiguration> featurePlaceContext, CallbackInfoReturnable<Boolean> cir) {
        FeatureMixinHolder.wasWorldgen = false;
    }
}