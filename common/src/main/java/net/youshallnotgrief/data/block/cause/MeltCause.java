package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MeltCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#melt";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append((blockComponent).append(" melted"));
    }
}
