package net.youshallnotgrief.util.mixin;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface PlayerMenuContext {
    void youshallnotgrief$setContainerPos(BlockPos pos);

    @Nullable
    BlockPos youshallnotgrief$getContainerPos();
}
