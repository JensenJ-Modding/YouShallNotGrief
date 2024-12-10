package net.youshallnotgrief.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;

public class EntityUtils {

    public static String getSourceAndTargets(LivingEntity sourceEntity){
        StringBuilder sourceDesc = new StringBuilder();
        if(sourceEntity != null){
            sourceDesc = new StringBuilder("mob;" + sourceEntity.getName().getString() + ";");
        }
        if (sourceEntity instanceof Mob mob){
            MobTargetInterface targetInterface = (MobTargetInterface) mob;
            ArrayList<LivingEntity> targets = targetInterface.youshallnotgrief$getTargetedMobs();

            for(LivingEntity target : targets) {
                if (target != null) {
                    sourceDesc.append(target.getName().getString()).append(";");
                }
            }
        }
        return sourceDesc.toString();
    }
}

