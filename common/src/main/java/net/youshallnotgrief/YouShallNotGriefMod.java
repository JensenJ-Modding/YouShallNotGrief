package net.youshallnotgrief;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.event.BlockEvents;
import net.youshallnotgrief.util.CommandManager;
import net.youshallnotgrief.util.InspectionMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouShallNotGriefMod {
    public static final String MOD_ID = "youshallnotgrief";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        registerEvents();
    }

    public static void registerEvents(){
        DatabaseManager.registerLifecycleEvents();
        CommandManager.registerCommands();
        InspectionMode.registerEvents();
        BlockEvents.registerEvents();

        InteractionEvent.RIGHT_CLICK_BLOCK.register((Player var1, InteractionHand var2, BlockPos var3, Direction var4) ->{
            return EventResult.pass();
        });

        InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) -> {
            return EventResult.pass();
        });

        InteractionEvent.RIGHT_CLICK_ITEM.register((Player player, InteractionHand hand) -> {
            return CompoundEventResult.pass();
        });

        //Entity Events
        EntityEvent.LIVING_DEATH.register((LivingEntity entity, DamageSource source) -> {
            return EventResult.pass();
        });

        EntityEvent.LIVING_HURT.register((LivingEntity entity, DamageSource source, float amount) -> {
            return EventResult.pass();
        });

        EntityEvent.ANIMAL_TAME.register((Animal animal, Player player) -> {
            return EventResult.pass();
        });

        //Mixins
        //  Fire
        //  Adding and removing items from inventory
        //  Non player break / place, maybe remove block events and just mixin directly

        //EntityEvent
        //  Entity hurt / death
        //  Entity tame

        //InteractionEvents
        //  RightClickBlock
        //    Door
        //    Anything that has a menu, chest, furnace, enchant table etc.
        //    Redstone components, button, lever, comparator, repeater
        //    Sign Edit (1.20)
        //  RightClickItem
        //    Waxable Block (waxing and stripping)
        //  InteractEntity
        //    Item Frame

    }
}
