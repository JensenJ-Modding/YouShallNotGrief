package net.youshallnotgrief.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {
    @SuppressWarnings("all")
    @Inject(method="checkWalls", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void youshallnotgrief$logEnderDragonBreakBlock(AABB aABB, CallbackInfoReturnable<Boolean> cir, @Local(ordinal=0) BlockPos blockPos){
        EnderDragon dragon = (EnderDragon) (Object) this;
        Level level = dragon.level();
        if(!level.isClientSide()){
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos.immutable(), level, BlockSetAction.REMOVED, dragon, ""));
        }
    }

}
