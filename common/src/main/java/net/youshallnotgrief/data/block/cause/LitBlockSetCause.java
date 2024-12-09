package net.youshallnotgrief.data.block.cause;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LitBlockSetCause extends BlockSetCause{
    public LitBlockSetCause(String causeName) {
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
