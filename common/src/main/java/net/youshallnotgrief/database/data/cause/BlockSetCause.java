package net.youshallnotgrief.database.data.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BlockSetCause {

    protected final String databaseTag;
    protected final String inspectionTranslationKey;

    public BlockSetCause(String causeName){
        causeName = causeName.toLowerCase();
        this.databaseTag = "#" + causeName;
        this.inspectionTranslationKey = "msg.youshallnotgrief.inspection.block.cause." + causeName;
    }

    public String getDatabaseTag(){
        return databaseTag;
    }

    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        return Component.translatable(inspectionTranslationKey, oldBlockComponent, newBlockComponent, sourceComponent);
    }
}
