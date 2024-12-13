package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LightningBolt.class, priority = 10100)
public class LightningBoltMixin {

    @Unique
    private static LightningBolt youshallnotgrief$capturedBolt;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    public void youshallnotgrief$captureBolt(CallbackInfo ci){
        youshallnotgrief$capturedBolt = (LightningBolt) (Object) this;
    }


    @WrapOperation(method = "spawnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logLightning(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        LightningBolt bolt = (LightningBolt) (Object) this;

        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logLightning.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PLACED, bolt, "");
            }
        });
    }

    @WrapOperation(method = "clearCopperOnLightningStrike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean youshallnotgrief$logLightningCopperInteraction1(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logLightning.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, youshallnotgrief$capturedBolt, "");
            }
        });
    }

    @WrapOperation(method = "method_34708", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean youshallnotgrief$logLightningCopperInteraction2(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logLightning.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, youshallnotgrief$capturedBolt, "");
            }
        });
    }
}
