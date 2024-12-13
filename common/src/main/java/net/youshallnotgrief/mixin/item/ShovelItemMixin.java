package net.youshallnotgrief.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShovelItem.class, priority = 10100)
public abstract class ShovelItemMixin {

    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean youshallnotgrief$logPathFlattening(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original, @Local(argsOnly = true) UseOnContext context) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(oldState.getBlock() == Blocks.CAMPFIRE){
                if(ServerConfig.logExtinguish.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.EXTINGUISH, context.getPlayer(), "");
                }
            }else{
                if(ServerConfig.logPaving.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PAVED, context.getPlayer(), "");
                }
            }
        });
    }
}
