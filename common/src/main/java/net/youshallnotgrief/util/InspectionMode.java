package net.youshallnotgrief.util;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.data.block.BlockSetData;
import net.youshallnotgrief.data.block.BlockSetQueryData;
import net.youshallnotgrief.database.DatabaseManager;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.*;

public class InspectionMode {

    public static HashSet<Player> INSPECTING_PLAYERS = new HashSet<>();
    private static final HashMap<Player, BlockPos> CURRENTLY_SELECTED_BLOCK = new HashMap<>();

    private static final int ACTIONS_PER_PAGE = 8;
    private static final int MAX_CHARACTERS_PER_LINE = 53;

    public static void registerEvents(){
        InteractionEvent.RIGHT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) ->{
            if(player.level().isClientSide){
                return EventResult.pass();
            }
            if(hand == InteractionHand.OFF_HAND){
                return EventResult.pass();
            }
            if(!INSPECTING_PLAYERS.contains(player)){
                return EventResult.pass();
            }

            CURRENTLY_SELECTED_BLOCK.put(player, pos);
            showDetails(player, 0);
            return EventResult.interruptFalse();
        });

        PlayerEvent.CHANGE_DIMENSION.register((ServerPlayer player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) -> CURRENTLY_SELECTED_BLOCK.remove(player));
        PlayerEvent.PLAYER_QUIT.register(CURRENTLY_SELECTED_BLOCK::remove);
    }

    public static void toggleInspectMode(Player player){
        if(INSPECTING_PLAYERS.contains(player)){
            INSPECTING_PLAYERS.remove(player);
            player.sendSystemMessage(Component.literal("Exited inspection mode").withStyle(ChatFormatting.DARK_AQUA));
        }else{
            INSPECTING_PLAYERS.add(player);
            player.sendSystemMessage(Component.literal("Entered inspection mode").withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    public static void showDetails(Player player, int pageNumber){
        if(!InspectionMode.INSPECTING_PLAYERS.contains(player)){
            player.sendSystemMessage(Component.literal("You must be in inspect mode to do this.").withStyle(ChatFormatting.RED));
            return;
        }

        if(!CURRENTLY_SELECTED_BLOCK.containsKey(player)){
            player.sendSystemMessage(Component.literal("You must inspect something first.").withStyle(ChatFormatting.RED));
            return;
        }

        DatabaseManager.commitAllQueuedDataToDatabase();
        BlockPos pos = CURRENTLY_SELECTED_BLOCK.get(player);
        String dimensionName = BlockUtils.getDimensionNameFromLevel(player.level());
        Future<RetrieveResult<BlockSetData>> futureData = DatabaseManager.BLOCK_SET_MANAGER.retrieveFromDatabase(new BlockSetQueryData(pos, dimensionName), ACTIONS_PER_PAGE, pageNumber * ACTIONS_PER_PAGE);

        try {
            RetrieveResult<BlockSetData> retrieveResult = futureData.get(5, TimeUnit.SECONDS);
            ArrayList<BlockSetData> data = retrieveResult.getRecords();
            int count = retrieveResult.getCount();

            if(count == 0){
                player.sendSystemMessage(Component.literal("No data was found for the selected block.").withStyle(ChatFormatting.RED));
                return;
            }

            int maxPageCount = (int) Math.ceil((double) count / ACTIONS_PER_PAGE);
            if(pageNumber >= maxPageCount){
                player.sendSystemMessage(Component.literal("This block does not have that many entries. It currently has a maximum of " + maxPageCount + ".").withStyle(ChatFormatting.RED));
                return;
            }

            player.sendSystemMessage(getHeader(player.level(), pos, dimensionName));
            for (BlockSetData datum : data) {
                player.sendSystemMessage(getData(datum));
            }

            player.sendSystemMessage(getFooter(pageNumber, maxPageCount));

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            YouShallNotGriefMod.LOGGER.info("{} failed to inspect page {} of block at coordinates {} {} {} in {}. The database failed to retrieve data for this location.",
                    player.getName().getString(), pageNumber, pos.getX(), pos.getY(), pos.getZ(), dimensionName);
            return;
        }

        YouShallNotGriefMod.LOGGER.info("{} inspected page {} of block at coordinates {} {} {} in {}",
                player.getName().getString(), pageNumber, pos.getX(), pos.getY(), pos.getZ(), dimensionName);
    }

    private static Component getHeader(Level level, BlockPos pos, String dimensionName){
        MutableComponent blockComp = Component.literal(BlockUtils.getBlockNameFromBlockPos(level, pos))
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(BlockUtils.getBlockIDFromBlockPos(level, pos))))
                );

        MutableComponent position = Component.literal("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))
                );

        MutableComponent dimension = Component.literal(dimensionName)
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))
                );

        MutableComponent comp = Component.empty();
        return comp.append(blockComp).append(" at ").append(position).append(" in ").append(dimension);
    }

    private static Component getData(BlockSetData data){
        MutableComponent timeComp = Component.literal(formatTimeAgo(data.time()))
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(formatTime(data.time()))))
                );

        MutableComponent sourceComp;
        if(data.blockSetSourceData().sourceDesc().isEmpty()){
            sourceComp = Component.literal(data.blockSetSourceData().source())
                    .withStyle(style -> style
                            .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                    );
        }else{
            sourceComp = Component.literal(data.blockSetSourceData().source())
                    .withStyle(style -> style
                            .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(data.blockSetSourceData().sourceDesc())))
                    );
        }

        MutableComponent actionComp = Component.literal(String.valueOf(data.action()).toLowerCase());

        MutableComponent blockComp = Component.literal(data.blockSetBlockData().blockName())
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(data.blockSetBlockData().blockInternalName())))
                );

        MutableComponent comp = Component.empty();
        return comp.append(timeComp).append(" - ").append(sourceComp).append(" ").append(actionComp).append(" ").append(blockComp);
    }

    private static Component getFooter(int currentPage, int maxPageCount) {
        String footerPageCount = String.format(" %d/%d ", currentPage + 1, maxPageCount);
        String previousPage = "<----";
        String nextPage = "---->";

        MutableComponent footerComp = Component.literal(footerPageCount)
                .withStyle(style -> style.withColor(ChatFormatting.GRAY));

        MutableComponent previousComp = Component.literal(previousPage)
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/page " + currentPage))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Previous Page")))
                );

        MutableComponent nextComp = Component.literal(nextPage)
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/page " + (currentPage + 2)))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Next Page")))
                );

        int footerLength = footerPageCount.length();
        int previousButtonLength = currentPage != 0 ? previousPage.length() : previousPage.replace("<-- ", "-").length();
        int nextButtonLength = currentPage < maxPageCount - 1 ? nextPage.length() : nextPage.replace(" -->", "-").length();

        int availableSpace = MAX_CHARACTERS_PER_LINE - (footerLength + previousButtonLength + nextButtonLength);
        int leftHyphens = availableSpace / 2;
        int rightHyphens = availableSpace - leftHyphens;

        MutableComponent comp = Component.empty();
        if (currentPage != 0) {
            comp = comp.append(previousComp);
        } else {
            comp = comp.append(Component.literal("-".repeat(previousButtonLength)));
        }

        comp = comp.append(Component.literal("-".repeat(Math.max(leftHyphens, 0))));
        comp = comp.append(footerComp);
        comp = comp.append(Component.literal("-".repeat(Math.max(rightHyphens, 0))));
        if (currentPage < maxPageCount - 1) {
            comp = comp.append(nextComp);
        } else {
            comp = comp.append(Component.literal("-".repeat(nextButtonLength)));
        }

        return comp;
    }


    private static String formatTime(Timestamp timestamp){
        LocalDateTime now = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        return now.format(formatter);
    }

    public static String formatTimeAgo(Timestamp timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp.toLocalDateTime(), now);

        long totalSeconds = duration.getSeconds();
        double days = totalSeconds / 86400.0;
        double hours = totalSeconds / 3600.0;
        double minutes = totalSeconds / 60.0;

        if (days >= 1) {
            return String.format("%.2fd ago", days);
        } else if (hours >= 1) {
            return String.format("%.2fh ago", hours);
        } else {
            return String.format("%.2fm ago", minutes);
        }
    }
}
