package net.youshallnotgrief.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class ExplosionUtils {

    //TODO: Redo this formatting
    public static String getSourceDescription(LivingEntity sourceEntity){
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

