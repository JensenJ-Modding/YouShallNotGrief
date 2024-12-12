package net.youshallnotgrief.util;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.data.block.BlockSetData;
import net.youshallnotgrief.data.block.BlockSetQueryData;
import net.youshallnotgrief.data.block.cause.BlockSetCause;
import net.youshallnotgrief.data.block.cause.BlockSetCauses;
import net.youshallnotgrief.database.DatabaseManager;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class InspectionMode {

    public static HashSet<Player> INSPECTING_PLAYERS = new HashSet<>();
    private static final HashMap<Player, BlockPos> CURRENTLY_SELECTED_BLOCK = new HashMap<>();

    private static final int ACTIONS_PER_PAGE = 8;
    private static final int MAX_CHARACTERS_PER_LINE = 53;

    public static void registerEvents(){
        InteractionEvent.LEFT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) ->{
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

        InteractionEvent.RIGHT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) -> {
            if(player.level().isClientSide){
                return EventResult.pass();
            }
            if(hand == InteractionHand.OFF_HAND){
                return EventResult.pass();
            }
            if(!INSPECTING_PLAYERS.contains(player)){
                return EventResult.pass();
            }

            CURRENTLY_SELECTED_BLOCK.put(player, pos.relative(face, 1));
            showDetails(player, 0);
            return EventResult.interruptFalse();
        });

        PlayerEvent.CHANGE_DIMENSION.register((ServerPlayer player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) -> CURRENTLY_SELECTED_BLOCK.remove(player));
        PlayerEvent.PLAYER_QUIT.register(CURRENTLY_SELECTED_BLOCK::remove);
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

        if(!CURRENTLY_SELECTED_BLOCK.containsKey(player)){
            player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.inspectfirst").withStyle(style -> style
                    .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
            return;
        }

        DatabaseManager.commitAllQueuedDataToDatabase();
        BlockPos pos = CURRENTLY_SELECTED_BLOCK.get(player);
        String dimensionID = MiscUtils.getDimensionIDFromLevel(player.level());
        Future<RetrieveResult<BlockSetData>> futureData = DatabaseManager.BLOCK_SET_MANAGER.retrieveFromDatabase(new BlockSetQueryData(pos, dimensionID), ACTIONS_PER_PAGE, pageNumber * ACTIONS_PER_PAGE);

        try {
            RetrieveResult<BlockSetData> retrieveResult = futureData.get(5, TimeUnit.SECONDS);
            ArrayList<BlockSetData> data = retrieveResult.records();
            int count = retrieveResult.count();

            if(count == 0){
                player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.nodata").withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
                return;
            }

            int maxPageCount = (int) Math.ceil((double) count / ACTIONS_PER_PAGE);
            if(pageNumber >= maxPageCount){
                player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.invalidpage", Component.literal(String.valueOf(maxPageCount))).withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
                return;
            }

            player.sendSystemMessage(getHeader(player.level(), pos, dimensionID));
            for (BlockSetData datum : data) {
                Component dataToSend = getData(datum);
                if(dataToSend != null){
                    player.sendSystemMessage(dataToSend);
                }
            }

            player.sendSystemMessage(getFooter(pageNumber, maxPageCount));

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            YouShallNotGriefMod.LOGGER.error("{} failed to inspect page {} of block at coordinates {} {} {} in {}. The database failed to retrieve data for this location.",
                    player.getName().getString(), pageNumber + 1, pos.getX(), pos.getY(), pos.getZ(), dimensionID);
            return;
        }

        YouShallNotGriefMod.LOGGER.info("{} inspected page {} of block at coordinates {} {} {} in {}",
                player.getName().getString(), pageNumber + 1, pos.getX(), pos.getY(), pos.getZ(), dimensionID);
    }

    private static Component getHeader(Level level, BlockPos pos, String dimensionID){
        MutableComponent blockComp = Component.literal(BlockUtils.getBlockNameFromBlockState(level.getBlockState(pos)))
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(BlockUtils.getBlockIDFromBlockState(level.getBlockState(pos)))))
                );

        MutableComponent position = Component.literal("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                );

        MutableComponent dimension = Component.literal(dimensionID)
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                );

        return Component.translatable("msg.youshallnotgrief.inspection.header", blockComp, position, dimension).withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));
    }

    private static Component getData(BlockSetData data){
        MutableComponent timeComp = formatTimeAgo(data.time())
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionSecondaryColour.get()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(formatTime(data.time()))))
                );

        MutableComponent oldBlockComp = getBlockComponentFromString(data.oldBlock());
        MutableComponent newBlockComp = getBlockComponentFromString(data.newBlock());

        String source = data.source();
        BlockSetCause cause = BlockSetCauses.getCauseFromTag(data.cause());
        if(cause == null){
            YouShallNotGriefMod.LOGGER.warn("Tried to show logs for a block cause which does not exist: {}.", data.cause());
            return null;
        }

        MutableComponent sourceComp = getSourceComponentFromString(source, data.sourceDesc());
        MutableComponent comp = Component.empty().append(timeComp).append(" - ").withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));

        return comp.append(cause.getInspectMessage(oldBlockComp, newBlockComp, sourceComp));
    }

    private static Component getFooter(int currentPage, int maxPageCount) {
        String footerPageCount = String.format(" %d/%d ", currentPage + 1, maxPageCount);
        String previousPage = "<----";
        String nextPage = "---->";

        MutableComponent footerComp = Component.literal(footerPageCount)
                .withStyle(style -> style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionSecondaryColour.get())));

        MutableComponent previousComp = Component.literal(previousPage)
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/page " + currentPage))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Previous Page")))
                );

        MutableComponent nextComp = Component.literal(nextPage)
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
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
            comp = comp.append(Component.literal("-".repeat(previousButtonLength)).withStyle(style ->
                    style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get()))));
        }

        comp = comp.append(Component.literal("-".repeat(Math.max(leftHyphens, 0))).withStyle(style ->
                style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get()))));
        comp = comp.append(footerComp);
        comp = comp.append(Component.literal("-".repeat(Math.max(rightHyphens, 0))).withStyle(style ->
                style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get()))));
        if (currentPage < maxPageCount - 1) {
            comp = comp.append(nextComp);
        } else {
            comp = comp.append(Component.literal("-".repeat(nextButtonLength))).withStyle(style ->
                    style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));
        }

        return comp;
    }

    private static String formatTime(Timestamp timestamp){
        LocalDateTime now = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ServerConfig.inspectionFullTimeFormat.get());
        return now.format(formatter);
    }

    private static MutableComponent formatTimeAgo(Timestamp timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp.toLocalDateTime(), now);

        long totalSeconds = duration.getSeconds();
        double days = totalSeconds / 86400.0;
        double hours = totalSeconds / 3600.0;
        double minutes = totalSeconds / 60.0;

        String timeFormat = "%." + ServerConfig.inspectionTimePrecision.get().toString() + "f";
        if (days >= 1) {
            return Component.literal(String.format(timeFormat, days)).append(Component.translatable("msg.youshallnotgrief.inspection.time.days"));
        } else if (hours >= 1) {
            return Component.literal(String.format(timeFormat, hours)).append(Component.translatable("msg.youshallnotgrief.inspection.time.hours"));
        } else {
            return Component.literal(String.format(timeFormat, minutes)).append(Component.translatable("msg.youshallnotgrief.inspection.time.minutes"));
        }
    }

    private static MutableComponent getBlockComponentFromString(String blockName){
        Block block = BlockUtils.getBlockFromString(blockName);
        if(block == null){
            return Component.literal(blockName)
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                    );
        }else{
            return Component.literal(BlockUtils.getBlockNameFromBlockState(block.defaultBlockState()))
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(blockName)))
                    );
        }
    }

    private static MutableComponent getSourceComponentFromString(String source, String sourceDesc){
        if(sourceDesc.isEmpty()){
            return Component.literal(source)
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                    );
        }else{
            //If the source description starts with mob, then we need to format it
            MutableComponent hoverComponent;
            if(sourceDesc.startsWith("mob;")){
                hoverComponent = formatMobSourceDesc(sourceDesc);
            } else {
                hoverComponent = Component.literal(sourceDesc);
            }

            return Component.literal(source)
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent))
                    );
        }
    }

    private static MutableComponent formatMobSourceDesc(String sourceDesc){
        if (sourceDesc == null || sourceDesc.isEmpty()) {
            return Component.literal(sourceDesc);
        }

        List<String> elements = Arrays.asList(sourceDesc.split(";"));
        MutableComponent comp = Component.translatable("msg.youshallnotgrief.inspection.block.cause.mob.source", elements.get(1));
        for(int i = 2; i < elements.size(); i++){
            comp.append("\n").append(Component.translatable("msg.youshallnotgrief.inspection.block.cause.mob.target", elements.get(1), elements.get(i)));
        }
        return comp;
    }
}
