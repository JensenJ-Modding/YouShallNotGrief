package net.youshallnotgrief.util.mixin;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

// This interface is used to store and access the position of the block that a menu is associated with
public interface EntityUUIDMenuContext {
    void youshallnotgrief$setContainerUUID(UUID id);

    @Nullable UUID youshallnotgrief$getContainerUUID();
}
