package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.mixin.BlockPositionMenuContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerPlayer.class, priority = 10100)
public class ServerPlayerMixin implements BlockPositionMenuContext {

    @Unique
    BlockPos youshallnotgrief$blockContainerPos = null;

    @WrapOperation(method = "createEndPlatform", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean youshallnotgrief$logPlayerCreateEndPlatform(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logEndFight.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.FALLBACK, player, "");
            }
        });
    }

    @Override
    public BlockPos youshallnotgrief$getContainerPos() {
        return youshallnotgrief$blockContainerPos;
    }

    @Override
    public void youshallnotgrief$setContainerPos(BlockPos pos) {
        youshallnotgrief$blockContainerPos = pos;
    }
}
