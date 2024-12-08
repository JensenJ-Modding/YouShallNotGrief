package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class InfestedCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#infested";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(sourceComponent).append(" infested ").append(oldBlockComponent);
    }
}
