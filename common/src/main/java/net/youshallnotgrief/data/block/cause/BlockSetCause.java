package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.MutableComponent;

public interface BlockSetCause {
    String getDatabaseTag();
    MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent);
}
