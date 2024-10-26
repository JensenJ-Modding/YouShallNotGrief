package net.youshallnotgrief.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Final
    @Shadow
    private Level level;

    @Shadow
    public Entity source;

    @Shadow @Nullable
    public abstract LivingEntity getIndirectSourceEntity();

    @SuppressWarnings("all")
    @Inject(method="finalizeExplosion", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/block/Block;wasExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void youshallnotgrief$logBlockExplosion(boolean bl, CallbackInfo ci, @Local BlockPos pos, @Local BlockState state){
        if(level.isClientSide()){
            return;
        }

        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromBlockState(state, pos, level, BlockSetAction.EXPLODED, source, youshallnotgrief$getSourceDescription()));
    }

    @SuppressWarnings("all")
    @Inject(method="finalizeExplosion", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void youshallnotgrief$logBlockFireExplosion(boolean bl, CallbackInfo ci, @Local BlockPos pos){
        if(level.isClientSide()){
            return;
        }
        BlockState state = BaseFireBlock.getState(this.level, pos);
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromBlockState(state, pos, level, BlockSetAction.PLACED, source, youshallnotgrief$getSourceDescription()));
    }

    @Unique
    private String youshallnotgrief$getSourceDescription(){
        LivingEntity sourceEntity = getIndirectSourceEntity();
        String sourceDesc = "";
        if(sourceEntity != null){
            sourceDesc = "Caused by " + sourceEntity.getName().getString();
        }
        if (sourceEntity instanceof Mob mob){
            LivingEntity target = mob.getTarget();
            //TODO: Log all previous targets of the mob, not just the current one
            if(target != null){
                sourceDesc += "\n" + mob.getName().getString() + " was targeting " + target.getName().getString();
            }
        }
        return sourceDesc;
    }
}
