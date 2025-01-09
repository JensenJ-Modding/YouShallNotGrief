package net.youshallnotgrief.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.youshallnotgrief.util.mixin.MobTargetInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

//Intermediary name is class_1308, used in architectury.common.json for interface injection
@Mixin(value = Mob.class, priority = 10100)
public class MobMixin implements MobTargetInterface {

    @Unique
    private final ArrayList<LivingEntity> youshallnotgrief$targets = new ArrayList<>();

    @Inject(method = "setTarget", at = @At(value = "HEAD"))
    private void youshallnotgrief$addTarget(LivingEntity livingEntity, CallbackInfo ci){
        //Don't add the target if the target has been set to null
        if(livingEntity == null){
            return;
        }

        //If the target list is empty, add the new target
        if(youshallnotgrief$targets.isEmpty()){
            youshallnotgrief$targets.add(livingEntity);
            return;
        }

        //Skip adding this target if it is the same as the last target
        if(youshallnotgrief$targets.get(youshallnotgrief$targets.size() - 1).getUUID() == livingEntity.getUUID()) {
            return;
        }

        youshallnotgrief$targets.add(livingEntity);
    }

    @Override
    public ArrayList<LivingEntity> youshallnotgrief$getTargetedMobs() {
        return youshallnotgrief$targets;
    }
}
