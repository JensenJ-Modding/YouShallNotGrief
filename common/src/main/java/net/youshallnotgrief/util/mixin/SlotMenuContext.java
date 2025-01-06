package net.youshallnotgrief.util.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;

//This interface is used to set the menu which a slot is in.
public interface SlotMenuContext {
    void youshallnotgrief$setContainer(AbstractContainerMenu menu);
}
