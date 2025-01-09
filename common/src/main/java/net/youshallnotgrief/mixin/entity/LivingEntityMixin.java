package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 10100)
public class LivingEntityMixin {
    @WrapOperation(method = "createWitherRose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean youshallnotgrief$logWitherRosePlacement(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logPlantGrowth.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PLACED, entity, "");
            }
        });
    }
}
