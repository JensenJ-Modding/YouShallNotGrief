package net.youshallnotgrief.util;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.database.data.BlockItemTransactionData;
import net.youshallnotgrief.database.data.EntityItemTransactionData;
import net.youshallnotgrief.database.data.SourceData;
import net.youshallnotgrief.database.manager.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryUtils {

    private static final Registrar<Item> ITEMS_REGISTRY = YouShallNotGriefMod.REGISTRY_MANAGER.get().get(Registries.ITEM);

    public static Item getItemFromString(String resourceLocation){
        return ITEMS_REGISTRY.get(new ResourceLocation(resourceLocation));
    }

    public static String getItemName(Item item){
        ResourceLocation location = item.arch$registryName();
        return location != null ? location.toString() : "";
    }

    public static void addToDatabase(@NotNull BlockPos pos, @NotNull Level level, @NotNull Item item, int amount, @Nullable Entity source, @NotNull String sourceDesc){
        String sourceText = "";
        if(source != null){
            sourceText = source.getName().getString();
        }

        BlockItemTransactionData data = new BlockItemTransactionData(Timestamp.valueOf(LocalDateTime.now()), pos.immutable(), MiscUtils.getDimensionIDFromLevel(level), getItemName(item), amount, new SourceData(sourceText, sourceDesc));
        DatabaseManager.addToDatabase(data);
    }

    public static void addToDatabase(@NotNull UUID entity, @NotNull Item item, int amount, @Nullable Entity source, @NotNull String sourceDesc){
        String sourceText = "";
        if(source != null){
            sourceText = source.getName().getString();
        }

        EntityItemTransactionData data = new EntityItemTransactionData(Timestamp.valueOf(LocalDateTime.now()), entity.toString(), getItemName(item), amount, new SourceData(sourceText, sourceDesc));
        DatabaseManager.addToDatabase(data);
    }
}
