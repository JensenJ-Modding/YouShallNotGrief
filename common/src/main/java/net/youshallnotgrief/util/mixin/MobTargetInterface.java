package net.youshallnotgrief.util.mixin;

import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public interface MobTargetInterface {
    default ArrayList<LivingEntity> youshallnotgrief$getTargetedMobs() {
        return new ArrayList<>();
    }
}
