package net.youshallnotgrief.mixin.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;

@Mixin(value = HarvestFarmland.class, priority = 10100)
public class HarvestFarmlandMixin {

    @WrapOperation(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean youshallnotgrief$logVillagerHarvestFarmland(
            ServerLevel level, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original) {
        return BlockUtils.wrapLevelDestroyBlock(level, pos, b, entity, original, oldState -> {
            if (ServerConfig.logVillagerHarvesting.get()) {
                BlockUtils.addToDatabase(
                        pos, level, oldState, Blocks.AIR.defaultBlockState(), BlockSetCauses.REMOVED, entity, "");
            }
        });
    }

    @WrapOperation(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean youshallnotgrief$logVillagerPlaceCrops(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            Operation<Boolean> original,
            @Local(argsOnly = true) Villager entity) {
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if (ServerConfig.logVillagerHarvesting.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PLACED, entity, "");
            }
        });
    }
}
