package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GrowCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#grow";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append((blockComponent).append(" grew"));
    }
}