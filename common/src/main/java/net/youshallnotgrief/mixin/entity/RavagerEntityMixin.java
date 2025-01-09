package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Ravager.class, priority = 10100)
public abstract class RavagerEntityMixin {

    @WrapOperation(method="aiStep", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    public boolean youshallnotgrief$logRavagerBreakingLeaves(Level level, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original) {
        Ravager ravager = (Ravager) entity;
        String sourceDesc = EntityUtils.getSourceAndTargets(ravager);
        return BlockUtils.wrapLevelDestroyBlock(level, pos, b, entity, original, oldState -> {
            if(ServerConfig.logRavagerGriefing.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, ravager, sourceDesc);
            }
        });
    }
}
