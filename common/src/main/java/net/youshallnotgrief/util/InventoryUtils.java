package net.youshallnotgrief.util;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.database.data.BlockItemTransactionData;
import net.youshallnotgrief.database.data.EntityItemTransactionData;
import net.youshallnotgrief.database.data.SourceData;
import net.youshallnotgrief.database.manager.DatabaseManager;
import net.youshallnotgrief.inspection.ComponentUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryUtils {

    private static final Registrar<Item> ITEMS_REGISTRY = YouShallNotGriefMod.REGISTRY_MANAGER.get().get(Registries.ITEM);

    public static String getItemID(Item item){
        ResourceLocation location = item.arch$registryName();
        return location != null ? location.toString() : "";
    }

    public static String getItemName(Item item){
        return item.getDescription().getString();
    }

    public static Item getItemFromString(String resourceLocation){
        try {
            return ITEMS_REGISTRY.get(new ResourceLocation(resourceLocation));
        } catch (ResourceLocationException exception){
            return null;
        }
    }

    public static void addToDatabase(@NotNull BlockPos pos, @NotNull Level level, @NotNull Item item, int amount, @Nullable Entity source, @NotNull String sourceDesc){
        String sourceText = "";
        if(source != null){
            sourceText = source.getName().getString();
        }

        BlockItemTransactionData data = new BlockItemTransactionData(Timestamp.valueOf(LocalDateTime.now()), pos.immutable(), MiscUtils.getDimensionIDFromLevel(level), getItemID(item), amount, new SourceData(sourceText, sourceDesc));
        DatabaseManager.addToDatabase(data);
    }

    public static void addToDatabase(@NotNull UUID entity, @NotNull Item item, int amount, @Nullable Entity source, @NotNull String sourceDesc){
        String sourceText = "";
        if(source != null){
            sourceText = source.getName().getString();
        }

        EntityItemTransactionData data = new EntityItemTransactionData(Timestamp.valueOf(LocalDateTime.now()), entity.toString(), getItemID(item), amount, new SourceData(sourceText, sourceDesc));
        DatabaseManager.addToDatabase(data);
    }

    public static Component formatDataForInspection(Timestamp timestamp, String item, int amount, SourceData sourceData){
        MutableComponent timeComp = ComponentUtils.formatTimeAgo(timestamp)
                .withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionSecondaryColour.get()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(ComponentUtils.formatTime(timestamp))))
                );

        MutableComponent itemComp = ComponentUtils.getItemComponentFromString(item, amount);
        MutableComponent sourceComp = ComponentUtils.getSourceComponentFromString(sourceData.source(), sourceData.sourceDesc());
        MutableComponent comp = Component.empty().append(timeComp).append(" - ").withStyle(style -> style
                .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionBackgroundColour.get())));

        MutableComponent operationComp;
        if(amount < 0){
            operationComp = Component.translatable("msg.youshallnotgrief.inspection.item.extract", sourceComp, itemComp);
        }else{
            operationComp = Component.translatable("msg.youshallnotgrief.inspection.item.insert", sourceComp, itemComp);
        }

        return comp.append(operationComp);
    }
}
