package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GolemCreationCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#golemcreation";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(oldBlockComponent).append(" was combined into a golem");
    }
}
