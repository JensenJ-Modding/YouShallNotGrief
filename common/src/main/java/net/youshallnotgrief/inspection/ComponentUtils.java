package net.youshallnotgrief.inspection;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.EntityUtils;
import net.youshallnotgrief.util.InventoryUtils;
import net.youshallnotgrief.util.MiscUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ComponentUtils {
    public static String formatTime(Timestamp timestamp){
        LocalDateTime now = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ServerConfig.inspectionFullTimeFormat.get());
        return now.format(formatter);
    }

    public static MutableComponent formatTimeAgo(Timestamp timestamp) {
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

    public static MutableComponent getBlockComponentFromString(String blockName){
        Block block = BlockUtils.getBlockFromString(blockName);
        if(block == null){
            return Component.literal(blockName)
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                    );
        }else{
            return Component.literal(BlockUtils.getBlockName(block.defaultBlockState()))
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(blockName)))
                    );
        }
    }

    public static MutableComponent getItemComponentFromString(String itemName, int amount){
        Item item = InventoryUtils.getItemFromString(itemName);
        MutableComponent comp = Component.empty();
        comp.append(Component.literal(Math.abs(amount) + " ").withStyle(style -> style
            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
        ));
        if(item == null){
            return comp.append(Component.literal(itemName)
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                    ));
        }else{
            return comp.append(Component.literal(InventoryUtils.getItemName(item))
                    .withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(itemName)))
                    ));
        }
    }

    public static MutableComponent getSourceComponentFromString(String source, String sourceDesc){
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

    public static MutableComponent formatMobSourceDesc(String sourceDesc){
        if (sourceDesc == null || sourceDesc.isEmpty()) {
            return Component.literal(sourceDesc);
        }

        List<String> elements = Arrays.asList(sourceDesc.split(";"));
        String sourceEntityName = formatEntityNameFromDatabase(elements.get(1));
        MutableComponent comp = Component.translatable("msg.youshallnotgrief.inspection.block.cause.mob.source", sourceEntityName);
        for(int i = 2; i < elements.size(); i++){
            String targetEntityName = formatEntityNameFromDatabase(elements.get(i));
            comp.append("\n").append(Component.translatable("msg.youshallnotgrief.inspection.block.cause.mob.target", sourceEntityName, targetEntityName));
        }
        return comp;
    }

    public static String formatEntityNameFromDatabase(String rawName){
        int lastIndex = rawName.lastIndexOf("#");
        if(lastIndex == -1){
            return tryGetEntityName(rawName);
        }

        String customName = rawName.substring(0, lastIndex);
        String mobName = rawName.substring(lastIndex + 1);

        return customName + " (" + tryGetEntityName(mobName) + ")";
    }

    public static String tryGetEntityName(String name) {
        EntityType<?> entity = EntityUtils.getEntityFromString(name);
        if (entity != null) {
            return entity.getDescription().getString();
        } else {
            return name;
        }
    }
}
