package net.youshallnotgrief.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCause;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = DoorBlock.class, priority = 10100)
public class DoorBlockMixin {

    @WrapOperation(
            method = "setOpen",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean youshallnotgrief$logDoorSetOpen(
            Level level,
            BlockPos pos,
            BlockState state,
            int i,
            Operation<Boolean> original,
            @Local(argsOnly = true) Entity entity,
            @Local(argsOnly = true) boolean bl) {
        BlockState oldState = level.getBlockState(pos);
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, s -> {
            if (ServerConfig.logMobOpenDoor.get()) {
                BlockSetCause cause = bl ? BlockSetCauses.OPENED : BlockSetCauses.CLOSED;
                BlockUtils.addToDatabase(pos, level, oldState, state, cause, entity, "");
            }
        });
    }
}
