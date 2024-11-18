package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LitCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#lit";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(sourceComponent).append(" lit ").append(newBlockComponent);
    }
}
