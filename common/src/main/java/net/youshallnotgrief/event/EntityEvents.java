package net.youshallnotgrief.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.util.mixin.BlockPositionMenuContext;
import net.youshallnotgrief.util.mixin.EntityUUIDMenuContext;

public class EntityEvents {

    public static void registerEvents() {
        //Used to clear which inventory the player is currently accessing, which prevents players being associated with unrelated actions
        PlayerEvent.CLOSE_MENU.register((Player player, AbstractContainerMenu menu) -> {
            if(player.level().isClientSide){
                return;
            }

            //This is important as some modded menu implementation force the inventory closed before opening a menu.
            if(menu instanceof InventoryMenu){
                return;
            }

            ((BlockPositionMenuContext) player).youshallnotgrief$setContainerPos(null);
            ((EntityUUIDMenuContext) player).youshallnotgrief$setContainerUUID(null);
        });

        InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) -> {
            Level level = player.level();
            if(level.isClientSide){
                return EventResult.pass();
            }

            ((EntityUUIDMenuContext) player).youshallnotgrief$setContainerUUID(entity.getUUID());
            if(ServerConfig.logContainerAccesses.get()){
                //TODO: Implement container access logging for entities
            //    BlockState state = level.getBlockState(pos);
            //    BlockUtils.addToDatabase(pos, level, state, state, BlockSetCauses.ACCESSED, player, "");
            }

            return EventResult.pass();
        });

        EntityEvent.LIVING_DEATH.register((LivingEntity entity, DamageSource source) -> {
            return EventResult.pass();
        });

        EntityEvent.LIVING_HURT.register((LivingEntity entity, DamageSource source, float amount) -> {
            return EventResult.pass();
        });

        EntityEvent.ANIMAL_TAME.register((Animal animal, Player player) -> {
            return EventResult.pass();
        });


    }
}
