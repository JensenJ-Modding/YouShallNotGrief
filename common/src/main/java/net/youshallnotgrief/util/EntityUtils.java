package net.youshallnotgrief.util;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.mixin.MobTargetInterface;

import java.util.ArrayList;

public class EntityUtils {

    private static final Registrar<EntityType<?>> ENTITY_REGISTRY = YouShallNotGriefMod.REGISTRY_MANAGER.get().get(Registries.ENTITY_TYPE);

    public static String getEntityIDFromEntity(EntityType<?> entity){
        ResourceLocation location = entity.arch$registryName();
        return location != null ? location.toString() : "";
    }

    public static String getEntityName(Entity entity){
        return entity.getDisplayName().getString();
    }

    public static String getEntityCustomNameOrFallbackID(Entity entity){
        if(entity.hasCustomName() || entity instanceof Player){
            return getEntityName(entity) + "#" + getEntityIDFromEntity(entity.getType());
        }else{
            return getEntityIDFromEntity(entity.getType());
        }
    }

    public static EntityType<?> getEntityFromString(String resourceLocation){
        try {
            return ENTITY_REGISTRY.get(new ResourceLocation(resourceLocation));
        }catch (ResourceLocationException exception){
            return null;
        }
    }

    public static String getSourceAndTargets(LivingEntity sourceEntity){
        StringBuilder sourceDesc = new StringBuilder();
        if(sourceEntity != null){
            sourceDesc = new StringBuilder("mob;" + getEntityCustomNameOrFallbackID(sourceEntity) + ";");
        }
        if (sourceEntity instanceof Mob mob){
            MobTargetInterface targetInterface = (MobTargetInterface) mob;
            ArrayList<LivingEntity> targets = targetInterface.youshallnotgrief$getTargetedMobs();

            for(LivingEntity target : targets) {
                if (target != null) {
                    sourceDesc.append(getEntityCustomNameOrFallbackID(target)).append(";");
                }
            }
        }
        return sourceDesc.toString();
    }
}

