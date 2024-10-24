package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class RavagerCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#ravager";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(getDatabaseTagComponent()).append(" removed ").append(blockComponent);
    }
}