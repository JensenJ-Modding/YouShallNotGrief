package net.youshallnotgrief.mixin.blocks.plant;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BambooSaplingBlock.class, priority = 10100)
public class BambooSaplingBlockMixin {

    @WrapOperation(method = "growBamboo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean youshallnotgrief$logBambooSaplingGrow(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original){
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logPlantGrowth.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.GROW, null, "");
            }
        });
    }
}