package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ChangedCause implements BlockSetCause
{
    @Override
    public String getDatabaseTag() {
        return "#changed";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        return comp.append(oldBlockComponent).append(" changed to ").append(newBlockComponent);
    }
}
