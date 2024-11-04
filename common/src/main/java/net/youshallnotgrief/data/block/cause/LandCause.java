package net.youshallnotgrief.data.block.cause;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public class LandCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#land";
    }

    @Override
    public MutableComponent getDatabaseTagComponent()
    {
        return Component.literal("#gravity")
                .withStyle(style -> style
                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))
                );
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(getDatabaseTagComponent()).append(" caused ").append(newBlockComponent).append(" to land");
    }
}
