package net.youshallnotgrief.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EndDragonFight.class, priority = 10100)
public class EndDragonFightMixin {

    @WrapOperation(method="setDragonKilled", at = @At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logDragonEggSpawn(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original, @Local(argsOnly = true) EnderDragon dragon) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logEndFight.get() || ServerConfig.logEnderDragonGriefing.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.HATCHED, dragon, "");
            }
        });
    }

    @WrapOperation(method="respawnDragon", at = @At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logDragonRespawn(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logEndFight.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, null, "");
            }
        });
    }
}
