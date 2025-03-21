package net.youshallnotgrief.util.mixin;

import java.util.ArrayList;

import net.minecraft.world.entity.LivingEntity;

public interface MobTargetInterface {
    default ArrayList<LivingEntity> youshallnotgrief$getTargetedMobs() {
        return new ArrayList<>();
    }
}
