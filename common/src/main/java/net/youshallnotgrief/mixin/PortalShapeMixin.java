package net.youshallnotgrief.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.cause.BlockSetCauses;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PortalShape.class, priority = 10100)
public class PortalShapeMixin {

    @WrapOperation(method = "method_30488", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean youshallnotgrief$logPortalBlockPlacement(LevelAccessor levelAccessor, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        return BlockUtils.wrapLevelSetBlock(levelAccessor, pos, state, i, original, oldState -> {
            if(levelAccessor instanceof Level level) {
                if(ServerConfig.logPortals.get()) {
                    BlockUtils.addToDatabase(pos, level, oldState, state, BlockSetCauses.PORTAL, null, "");
                }
            }
        });
    }
}
