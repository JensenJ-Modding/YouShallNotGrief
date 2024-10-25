package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class UnsupportedCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#unsupported";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append("No support caused ").append(blockComponent).append(" to break");
    }
}
