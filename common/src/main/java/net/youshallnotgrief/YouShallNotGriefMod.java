package net.youshallnotgrief;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.utils.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.youshallnotgrief.data.BlockSetActions;
import net.youshallnotgrief.data.BlockSetData;
import net.youshallnotgrief.data.BlockSetQueryData;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class YouShallNotGriefMod {
    public static final String MOD_ID = "youshallnotgrief";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    public static void init() {
        registerEvents();
    }

    public static void registerEvents(){
        //Block Events
        BlockEvent.BREAK.register((Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) -> {
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetActions.REMOVED, player, ""));
            return EventResult.pass();
        });

        BlockEvent.PLACE.register((Level level, BlockPos pos, BlockState state, @Nullable Entity placer) -> {
            if(level.isClientSide()){
                return EventResult.pass();
            }
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetActions.PLACED, placer, ""));
            return EventResult.pass();
        });

        BlockEvent.FALLING_LAND.register((Level level, BlockPos pos, BlockState fallState, BlockState landOn, FallingBlockEntity entity) ->
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetActions.FELL, "", "")));

        //Player events
        PlayerEvent.FILL_BUCKET.register((Player player, Level level, ItemStack stack, @Nullable HitResult target) -> {
            //System.out.println("Fill bucket");
            if(target != null) {
                BlockPos pos = new BlockPos(new Vec3i((int) target.getLocation().x, (int) target.getLocation().y, (int) target.getLocation().z));
                DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetActions.BUCKETED, "", ""));
            }
            return CompoundEventResult.pass();
        });

        //Interaction events
        InteractionEvent.FARMLAND_TRAMPLE.register((Level level, BlockPos pos, BlockState state, float distance, Entity entity) -> {
            //System.out.println("Trample");
            DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(pos, level, BlockSetActions.TRAMPLED, entity, ""));
            return EventResult.pass();
        });

        InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) -> {
            //System.out.println("player interact");
            return EventResult.pass();
        });

        InteractionEvent.RIGHT_CLICK_ITEM.register((Player player, InteractionHand hand) -> {
            //System.out.println("click item on block");
            return CompoundEventResult.pass();
        });

        InteractionEvent.RIGHT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) ->{
            if(player.level().isClientSide){
                return EventResult.pass();
            }
            if(hand == InteractionHand.OFF_HAND){
                return EventResult.pass();
            }

            //TEMPORARY: force queued data to database
            DatabaseManager.commitAllQueuedDataToDatabase();

            ArrayList<BlockSetData> data = DatabaseManager.BLOCK_SET_MANAGER.retrieveFromDatabase(new BlockSetQueryData(pos, BlockUtils.getDimensionNameFromLevel(player.level())));
            for (int i = 0; i < data.size(); i++){
                LOGGER.info("{}: {} {} {}", i, data.get(i).blockName(), data.get(i).time(), data.get(i).source());
            }
            return EventResult.pass();
        });

        //Explosion Event
        ExplosionEvent.DETONATE.register((Level level, Explosion explosion, List<Entity> affectedEntities) -> {
            //System.out.println("Explosion");
            //May need to mixin to get affected blocks
        });

        //Entity Events
        EntityEvent.LIVING_DEATH.register((LivingEntity entity, DamageSource source) -> {
            //System.out.println("Entity death");
            return EventResult.pass();
        });

        EntityEvent.LIVING_HURT.register((LivingEntity entity, DamageSource source, float amount) -> {
            //System.out.println("Entity hurt");
            return EventResult.pass();
        });

        EntityEvent.ANIMAL_TAME.register((Animal animal, Player player) -> {
            //System.out.println("Entity tamed");
            return EventResult.pass();
        });

        DatabaseManager.registerLifecycleEvents();

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
