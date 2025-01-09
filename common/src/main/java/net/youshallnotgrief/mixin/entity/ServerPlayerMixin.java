package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.BlockPositionMenuContext;
import net.youshallnotgrief.util.mixin.EntityUUIDMenuContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ServerPlayer.class, priority = 10100)
public class ServerPlayerMixin implements BlockPositionMenuContext, EntityUUIDMenuContext {

    @Unique
    BlockPos youshallnotgrief$blockContainerPos = null;

    @Unique
    UUID youshallnotgrief$entityContainerID = null;

    @WrapOperation(method = "createEndPlatform", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logPlayerCreateEndPlatform(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logEndFight.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, player, "");
            }
        });
    }

    @Inject(method = "openHorseInventory", at = @At("TAIL"))
    private void youshallnotgrief$openRidingContainer(AbstractHorse abstractHorse, Container container, CallbackInfo ci){
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
