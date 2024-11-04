package net.youshallnotgrief.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EatBlockGoal.class)
public class EatBlockGoalMixin {

    @Final
    @Shadow
    private Level level;

    @Final
    @Shadow
    private Mob mob;

    @SuppressWarnings("all")
    @Inject(method="tick", at = @At(value="INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void youshallnotgrief$logAnimalEatGrass(CallbackInfo ci, BlockPos blockPos){
        if(!level.isClientSide()){
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos, level, null, Blocks.AIR.defaultBlockState(), BlockSetCauses.ATE, mob, ""));
        }
    }

    @SuppressWarnings("all")
    @Inject(method="tick", at = @At(value="INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void youshallnotgrief$logAnimalEatBlock(CallbackInfo ci, BlockPos blockPos, BlockPos blockPos2){
        if(!level.isClientSide()){
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos2, level, null, Blocks.DIRT.defaultBlockState(), BlockSetCauses.ATE, mob, ""));
        }
    }
}
