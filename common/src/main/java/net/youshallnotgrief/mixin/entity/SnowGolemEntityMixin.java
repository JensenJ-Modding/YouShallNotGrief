package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SnowGolem.class, priority = 10100)
public abstract class SnowGolemEntityMixin {

    @WrapOperation(method="aiStep", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logSnowGolemSnow(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        SnowGolem golem = ((SnowGolem) (Object) this);
        String sourceDesc = EntityUtils.getSourceAndTargets(golem);
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logSnowGolemWalking.get()) {
                BlockUtils.addToDatabase(pos, golem.level(), oldState, state, BlockSetCauses.PLACED, golem, sourceDesc);
            }
        });
    }
}
