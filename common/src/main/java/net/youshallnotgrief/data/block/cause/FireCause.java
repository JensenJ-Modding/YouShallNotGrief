package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FireCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#fire";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent blockComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(getDatabaseTagComponent()).append(" caused ").append(blockComponent).append(" to burn");
    }
}
