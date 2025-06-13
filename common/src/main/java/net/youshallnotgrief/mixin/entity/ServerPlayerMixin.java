package net.youshallnotgrief.mixin.entity;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.youshallnotgrief.util.mixin.BlockPositionMenuContext;
import net.youshallnotgrief.util.mixin.EntityUUIDMenuContext;
import org.jetbrains.annotations.Nullable;

@Mixin(value = ServerPlayer.class, priority = 10100)
public class ServerPlayerMixin implements BlockPositionMenuContext, EntityUUIDMenuContext {

    @Unique BlockPos youshallnotgrief$blockContainerPos = null;

    @Unique UUID youshallnotgrief$entityContainerID = null;

    @Inject(method = "openHorseInventory", at = @At("TAIL"))
    private void youshallnotgrief$openRidingContainer(
            AbstractHorse abstractHorse, Container container, CallbackInfo ci) {
        youshallnotgrief$setContainerUUID(abstractHorse.getUUID());
    }

    @Override
    public @Nullable BlockPos youshallnotgrief$getContainerPos() {
        return youshallnotgrief$blockContainerPos;
    }

    @Override
    public void youshallnotgrief$setContainerPos(BlockPos pos) {
        youshallnotgrief$blockContainerPos = pos;
    }

    @Override
    public void youshallnotgrief$setContainerUUID(UUID id) {
        youshallnotgrief$entityContainerID = id;
    }

    @Override
    public @Nullable UUID youshallnotgrief$getContainerUUID() {
        return youshallnotgrief$entityContainerID;
    }
}
