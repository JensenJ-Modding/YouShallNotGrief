package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class UsedCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#used";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(sourceComponent).append(" used ").append(oldBlockComponent);
    }
}