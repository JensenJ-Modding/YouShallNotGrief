package net.youshallnotgrief.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EnderEyeItem.class, priority = 10100)
public class EnderEyeItemMixin {

    @WrapOperation(method="useOn", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean youshallnotgrief$logEnderEyeUse(Level level, BlockPos pos, BlockState state, int i, Operation<Boolean> original, @Local(argsOnly = true) UseOnContext context) {
        return BlockUtils.wrapLevelSetBlock(level, pos, state, i, original, oldState -> {
            if(ServerConfig.logPortals.get()) {
                BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.LIT, context.getPlayer(), "");
            }
        });
    }
}

