package net.youshallnotgrief.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = FrostWalkerEnchantment.class, priority = 10100)
public abstract class FrostWalkerEnchantmentMixin {

    @WrapOperation(
            method = "onEntityMoved",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean youshallnotgrief$logFrostWalkerPlacement(
            Level level,
            BlockPos pos,
            BlockState state,
            Operation<Boolean> original,
            @Local(argsOnly = true) LivingEntity entity) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if (ServerConfig.logFrostWalker.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FROST_WALKER, entity, "");
            }
        });
    }
}
