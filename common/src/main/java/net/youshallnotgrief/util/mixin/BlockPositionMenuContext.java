package net.youshallnotgrief.util.mixin;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

//This interface is used to store and access the position of the block that a menu is associated with
public interface BlockPositionMenuContext {
    void youshallnotgrief$setContainerPos(BlockPos pos);
    @Nullable
    BlockPos youshallnotgrief$getContainerPos();

}
