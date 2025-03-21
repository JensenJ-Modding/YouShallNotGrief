package net.youshallnotgrief.inspection;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.CombinedBlockData;
import net.youshallnotgrief.database.data.EntityItemTransactionData;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;
import net.youshallnotgrief.util.MiscUtils;

public abstract class InspectionModeDisplay {

    protected static final int ACTIONS_PER_PAGE = 8;
    protected static final int MAX_CHARACTERS_PER_LINE = 53;

    public static void showDetailsForBlock(Player player, int pageNumber) {
        BlockPos pos = InspectionMode.getSelectedBlockPosForPlayer(player);
        String dimensionID = MiscUtils.getDimensionIDFromLevel(player.level());
        InspectionMode.getDataForBlock(pos, dimensionID, pageNumber, player, (retrieveResult) -> {
            if (retrieveResult == null) return;
            ArrayList<CombinedBlockData> data = retrieveResult.records();

            int count = retrieveResult.count();
            int maxPageCount = (int) Math.ceil((double) count / ACTIONS_PER_PAGE);
            if (guardPageErrors(player, count, pageNumber, maxPageCount)) return;

            player.sendSystemMessage(InspectionModeDisplay.getHeaderForBlock(player.level(), pos, dimensionID));
            sendFormattedBlockDataToPlayer(data, player);
            player.sendSystemMessage(getFooter(pageNumber, maxPageCount));

            YouShallNotGriefMod.LOGGER.info(
                    "{} inspected page {} of block at coordinates {} {} {} in {}",
                    player.getName().getString(),
                    pageNumber + 1,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    dimensionID);
        });
    }

    public static void showDetailsForEntity(Player player, int pageNumber) {
        Entity entity = InspectionMode.getSelectedEntityForPlayer(player);
        BlockPos pos = new BlockPos((int) entity.position().x, (int) entity.position().y, (int) entity.position().z);
        String dimensionID = MiscUtils.getDimensionIDFromLevel(player.level());
        InspectionMode.getDataForEntity(entity, pos, dimensionID, pageNumber, player, (retrieveResult) -> {
            if (retrieveResult == null) {
                return;
            }

            ArrayList<EntityItemTransactionData> data = retrieveResult.records();
            int count = retrieveResult.count();
            int maxPageCount = (int) Math.ceil((double) count / ACTIONS_PER_PAGE);

            if (guardPageErrors(player, count, pageNumber, maxPageCount)) return;

            player.sendSystemMessage(InspectionModeDisplay.getHeaderForEntity(entity, pos, dimensionID));
            sendFormattedEntityDataToPlayer(data, player);
            player.sendSystemMessage(getFooter(pageNumber, maxPageCount));

            YouShallNotGriefMod.LOGGER.info(
                    "{} inspected page {} of entity {} at coordinates {} {} {} in {}",
                    player.getName().getString(),
                    pageNumber + 1,
                    entity.getStringUUID(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    dimensionID);
        });
    }

    private static void sendFormattedBlockDataToPlayer(ArrayList<CombinedBlockData> data, Player player) {
        for (CombinedBlockData datum : data) {
            Component dataToSend;
            if (datum.blockData.oldBlock
                    == null) { // This is used as a basic test as oldBlock will be null in an entry returned from the
                // blockItemInteractionData table.
                dataToSend = datum.blockItemTransactionData.formatDataForInspection();
            } else {
                dataToSend = datum.blockData.formatDataForInspection();
            }
            if (dataToSend != null) {
                player.sendSystemMessage(dataToSend);
            }
        }
    }

    private static void sendFormattedEntityDataToPlayer(ArrayList<EntityItemTransactionData> data, Player player) {
        for (EntityItemTransactionData datum : data) {
            Component dataToSend = datum.formatDataForInspection();
            if (dataToSend != null) {
                player.sendSystemMessage(dataToSend);
            }
        }
    }

    private static boolean guardPageErrors(Player player, int count, int pageNumber, int maxPageCount) {
        if (count == 0) {
            player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.nodata")
                    .withStyle(style -> style.withColor(
                            MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
            return true;
        }

        if (pageNumber >= maxPageCount) {
            player.sendSystemMessage(Component.translatable(
                            "error.youshallnotgrief.inspection.invalidpage",
                            Component.literal(String.valueOf(maxPageCount)))
                    .withStyle(style -> style.withColor(
                            MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
            return true;
        }
        return false;
    }

    private static Component getHeaderForEntity(Entity entity, BlockPos pos, String dimensionID) {
        MutableComponent entityComp = Component.literal(EntityUtils.getEntityName(entity))
                .withStyle(style -> style.withColor(
                                MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal(EntityUtils.getEntityIDFromEntity(entity.getType())))));

        MutableComponent position = Component.literal("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                .withStyle(style ->
                        style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get())));

        MutableComponent dimension = Component.literal(dimensionID)
                .withStyle(style ->
                        style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get())));

        return Component.translatable("msg.youshallnotgrief.inspection.header", entityComp, position, dimension)
                .withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));
    }

    private static Component getHeaderForBlock(Level level, BlockPos pos, String dimensionID) {
        MutableComponent blockComp = Component.literal(BlockUtils.getBlockName(level.getBlockState(pos)))
                .withStyle(style -> style.withColor(
                                MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal(BlockUtils.getBlockID(level.getBlockState(pos))))));

        MutableComponent position = Component.literal("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                .withStyle(style ->
                        style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get())));

        MutableComponent dimension = Component.literal(dimensionID)
                .withStyle(style ->
                        style.withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get())));

        return Component.translatable("msg.youshallnotgrief.inspection.header", blockComp, position, dimension)
                .withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));
    }

    protected static Component getFooter(int currentPage, int maxPageCount) {
        String footerPageCount = String.format(" %d/%d ", currentPage + 1, maxPageCount);
        String previousPage = "<----";
        String nextPage = "---->";

        MutableComponent footerComp = Component.literal(footerPageCount)
                .withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionSecondaryColour.get())));

        MutableComponent previousComp = Component.literal(previousPage).withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/page " + currentPage))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Previous Page"))));

        MutableComponent nextComp = Component.literal(nextPage).withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/page " + (currentPage + 2)))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Next Page"))));

        int footerLength = footerPageCount.length();
        int previousButtonLength = currentPage != 0
                ? previousPage.length()
                : previousPage.replace("<-- ", "-").length();
        int nextButtonLength = currentPage < maxPageCount - 1
                ? nextPage.length()
                : nextPage.replace(" -->", "-").length();

        int availableSpace = MAX_CHARACTERS_PER_LINE - (footerLength + previousButtonLength + nextButtonLength);
        int leftHyphens = availableSpace / 2;
        int rightHyphens = availableSpace - leftHyphens;

        MutableComponent comp = Component.empty();
        if (currentPage != 0) {
            comp = comp.append(previousComp);
        } else {
            comp = comp.append(Component.literal("-".repeat(previousButtonLength))
                    .withStyle(style -> style.withColor(
                            MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get()))));
        }

        comp = comp.append(Component.literal("-".repeat(Math.max(leftHyphens, 0)))
                .withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get()))));
        comp = comp.append(footerComp);
        comp = comp.append(Component.literal("-".repeat(Math.max(rightHyphens, 0)))
                .withStyle(style -> style.withColor(
                        MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get()))));
        if (currentPage < maxPageCount - 1) {
            comp = comp.append(nextComp);
        } else {
            comp = comp.append(Component.literal("-".repeat(nextButtonLength)))
                    .withStyle(style -> style.withColor(
                            MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));
        }

        return comp;
    }
}
