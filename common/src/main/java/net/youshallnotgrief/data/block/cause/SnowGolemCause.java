package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SnowGolemCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#snowgolem";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(getDatabaseTagComponent()).append(" placed ").append(blockComponent);
    }
}
