package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EndCrystal.class, priority = 10100)
public class EndCrystalMixin {

    @WrapOperation(method="tick", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logEndCrystalFire(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            EndCrystal crystal = (EndCrystal) (Object) this;
            if(ServerConfig.logFire.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PLACED, crystal, "");
            }
        });
    }
}
