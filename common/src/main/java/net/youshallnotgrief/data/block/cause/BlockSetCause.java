package net.youshallnotgrief.data.block.cause;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public interface BlockSetCause {
    String getDatabaseTag();
    default MutableComponent getDatabaseTagComponent()
    {
        return Component.literal(getDatabaseTag())
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                );
    }
    MutableComponent getInspectMessage(MutableComponent blockComponent);
}
