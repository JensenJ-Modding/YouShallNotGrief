package net.youshallnotgrief.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.Level;

import net.youshallnotgrief.YouShallNotGriefMod;

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
}
