package net.youshallnotgrief.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//A forge version of this does not exist on 1.20.1, as mixin 0.8.5 (provided by forge 1.20.1) doesn't seem to work with default method interface injection
//TODO: Fix in neoforge versions
@Mixin(value = ChangeOverTimeBlock.class, priority = 10100)
public interface ChangeOverTimeBlockMixin {

    @WrapOperation(method = "method_34726", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean youshallnotgrief$logChangeOverTimeBlock(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlockAndUpdate(level, pos, state, original, oldState -> {
            if(ServerConfig.logOxidization.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.OXIDIZATION, null, "");
            }
        });
    }
}
