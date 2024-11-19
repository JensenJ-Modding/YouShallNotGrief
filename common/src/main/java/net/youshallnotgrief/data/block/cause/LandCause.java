package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LandCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#land";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append("Gravity caused ").append(newBlockComponent).append(" to land");
    }
}
