package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ModdedCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#modded";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(sourceComponent).append(" set ").append(oldBlockComponent).append(" to ").append(newBlockComponent);
    }
}
