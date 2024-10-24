package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PortalCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#portal";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(getDatabaseTagComponent()).append(" materialised ").append(blockComponent);
    }
}
