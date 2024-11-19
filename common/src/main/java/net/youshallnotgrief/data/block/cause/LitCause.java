package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LitCause implements BlockSetCause {
    @Override
    public String getDatabaseTag() {
        return "#lit";
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        MutableComponent comp = Component.empty();
        if(sourceComponent.getString().isEmpty()){
            return comp.append(oldBlockComponent).append(" was lit");
        }
        return comp.append(sourceComponent).append(" lit ").append(newBlockComponent);
    }
}
