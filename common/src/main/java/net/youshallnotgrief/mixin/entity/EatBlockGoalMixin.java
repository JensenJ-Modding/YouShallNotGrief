package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EatBlockGoal.class, priority = 10100)
public class EatBlockGoalMixin {

    @Final
    @Shadow
    private Mob mob;

    @WrapOperation(method="tick", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean youshallnotgrief$logAnimalEatGrass(Level level, BlockPos pos, boolean b, Operation<Boolean> original){
        return BlockUtils.wrapLevelRemoveBlock(level, pos, b, original, oldState -> {
            if(ServerConfig.logMobEatBlock.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.ATE, mob, "");
            }
        });
    }

    @WrapOperation(method="tick", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logAnimalEatBlock(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logMobEatBlock.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, Blocks.DIRT.defaultBlockState(), BlockSetCauses.ATE, mob, "");
            }
        });
    }
}
