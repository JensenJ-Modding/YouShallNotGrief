package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

//To be used if the source of an interaction can be null.
public class BlockSetCauseWithSourceFallback extends BlockSetCause{
    public BlockSetCauseWithSourceFallback(String causeName) {
        super(causeName);
    }

    @Override
    public MutableComponent getInspectMessage(MutableComponent oldBlockComponent, MutableComponent newBlockComponent, MutableComponent sourceComponent) {
        if(sourceComponent.getString().isEmpty()){
            return Component.translatable(super.inspectionTranslationKey + ".fallback", oldBlockComponent, newBlockComponent, sourceComponent);
        }

        return Component.translatable(super.inspectionTranslationKey, oldBlockComponent, newBlockComponent, sourceComponent);
    }
}
