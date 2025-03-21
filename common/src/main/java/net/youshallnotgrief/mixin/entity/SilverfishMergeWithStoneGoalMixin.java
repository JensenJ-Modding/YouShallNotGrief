package net.youshallnotgrief.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;

@Mixin(targets = "net.minecraft.world.entity.monster.Silverfish$SilverfishMergeWithStoneGoal", priority = 10100)
public class SilverfishMergeWithStoneGoalMixin {

    @WrapOperation(
            method = "start",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logSilverfishMerge(
            LevelAccessor levelAccessor, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        RandomStrollGoalAccessor accessor = (RandomStrollGoalAccessor) this;
        Mob silverfish = accessor.getMob();
        String sourceDesc = EntityUtils.getSourceAndTargets(silverfish);

        return BlockUtils.wrapLevelSetBlock(levelAccessor, pos, state, i, original, oldState -> {
            if (levelAccessor instanceof Level level) {
                if (ServerConfig.logMobInfestingBlock.get()) {
                    BlockUtils.addToDatabase(
                            pos, level, oldState, state, BlockSetCauses.INFESTED, silverfish, sourceDesc);
                }
            }
        });
    }
}
