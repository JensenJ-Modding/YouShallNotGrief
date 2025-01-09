package net.youshallnotgrief.inspection;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.manager.DatabaseManager;
import net.youshallnotgrief.util.MiscUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InspectionMode {

    public static final HashSet<Player> INSPECTING_PLAYERS = new HashSet<>();
    private static final HashMap<Player, BlockPos> CURRENTLY_SELECTED_BLOCK = new HashMap<>();
    private static final HashMap<Player, Entity> CURRENTLY_SELECTED_ENTITY = new HashMap<>();

    public static boolean guardInspectionModeInteraction(Player player, InteractionHand hand){
        if(player.level().isClientSide)
            return true;
        if(hand == InteractionHand.OFF_HAND)
            return true;
        return !INSPECTING_PLAYERS.contains(player);
    }

    public static void registerEvents(){
        InteractionEvent.LEFT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) ->{
            if(guardInspectionModeInteraction(player, hand))
                return EventResult.pass();

            CURRENTLY_SELECTED_BLOCK.put(player, pos);
            CURRENTLY_SELECTED_ENTITY.remove(player);
            showDetails(player, 0);
            return EventResult.interruptFalse();
        });

        InteractionEvent.RIGHT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) -> {
            if(guardInspectionModeInteraction(player, hand))
                return EventResult.pass();

            CURRENTLY_SELECTED_BLOCK.put(player, pos.relative(face, 1));
            CURRENTLY_SELECTED_ENTITY.remove(player);
            showDetails(player, 0);
            return EventResult.interruptFalse();
        });

        InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) -> {
            if(guardInspectionModeInteraction(player, hand))
                return EventResult.pass();

            CURRENTLY_SELECTED_ENTITY.put(player, entity);
            CURRENTLY_SELECTED_BLOCK.remove(player);
            showDetails(player, 0);
            return EventResult.interruptFalse();
        });

        PlayerEvent.ATTACK_ENTITY.register((Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) -> {
            if(guardInspectionModeInteraction(player, hand))
                return EventResult.pass();

            CURRENTLY_SELECTED_ENTITY.put(player, target);
            CURRENTLY_SELECTED_BLOCK.remove(player);
            showDetails(player, 0);
            return EventResult.interruptFalse();
        });

        PlayerEvent.CHANGE_DIMENSION.register((ServerPlayer player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) -> {
            CURRENTLY_SELECTED_BLOCK.remove(player);
            CURRENTLY_SELECTED_ENTITY.remove(player);
        });

        PlayerEvent.PLAYER_QUIT.register((ServerPlayer player) -> {
            CURRENTLY_SELECTED_BLOCK.remove(player);
            CURRENTLY_SELECTED_ENTITY.remove(player);
        });
    }

    public static void toggleInspectMode(Player player){
        if(INSPECTING_PLAYERS.contains(player)){
            disableInspectMode(player);
        }else{
            enableInspectMode(player);
        }
    }

    public static boolean isPlayerInspecting(Player player){
        return INSPECTING_PLAYERS.contains(player);
    }

    public static void enableInspectMode(Player player){
        INSPECTING_PLAYERS.add(player);
        player.sendSystemMessage(Component.translatable("msg.youshallnotgrief.inspection.enable").withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))));
        DatabaseManager.commitQueuedToDatabase();
    }

    public static void disableInspectMode(Player player){
        INSPECTING_PLAYERS.remove(player);
        player.sendSystemMessage(Component.translatable("msg.youshallnotgrief.inspection.disable").withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))));
    }

    public static void showDetails(Player player, int pageNumber){
        if(!InspectionMode.INSPECTING_PLAYERS.contains(player)){
            player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.outofinspection").withStyle(style -> style
                    .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
            return;
        }

        if(CURRENTLY_SELECTED_BLOCK.containsKey(player)){
            InspectionModeDisplay.showDetailsForBlock(player, pageNumber);
            return;
        }

        if(CURRENTLY_SELECTED_ENTITY.containsKey(player)){
            InspectionModeDisplay.showDetailsForEntity(player, pageNumber);
            return;
        }

        player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.inspectfirst").withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
    }

    protected static BlockPos getSelectedBlockPosForPlayer(Player player){
        return CURRENTLY_SELECTED_BLOCK.get(player);
    }

    protected static Entity getSelectedEntityForPlayer(Player player){
        return CURRENTLY_SELECTED_ENTITY.get(player);
    }
}
