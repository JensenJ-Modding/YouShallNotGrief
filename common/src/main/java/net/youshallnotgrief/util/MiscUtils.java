package net.youshallnotgrief.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.Level;

import dev.architectury.platform.Platform;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

public class MiscUtils {

    public static String getDimensionIDFromLevel(Level level) {
        return level.dimension().location().toString();
    }

    public static TextColor getTextColourFromConfig(String configColour) {
        try {
            return TextColor.parseColor(configColour).getOrThrow();
        } catch (IllegalStateException e) {
            YouShallNotGriefMod.LOGGER.error("Failed to load config value for colour: {}", configColour);
            return TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        }
    }

    public static void logIfEnabled(String message) {
        if (shouldLogDatabaseActions()) {
            YouShallNotGriefMod.LOGGER.info(message);
        }
    }

    public static void logIfEnabled(String message, Object... params) {
        if (shouldLogDatabaseActions()) {
            YouShallNotGriefMod.LOGGER.info(message, params);
        }
    }

    public static boolean shouldLogDatabaseActions() {
        return Platform.isDevelopmentEnvironment() || ServerConfig.debugLogDatabaseActions.get();
    }
}
