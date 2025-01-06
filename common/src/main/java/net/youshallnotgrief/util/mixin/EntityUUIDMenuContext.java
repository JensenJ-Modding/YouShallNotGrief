package net.youshallnotgrief.util.mixin;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//This interface is used to store and access the position of the block that a menu is associated with
public interface EntityUUIDMenuContext {
    void youshallnotgrief$setContainerUUID(UUID id);

    @Nullable
    UUID youshallnotgrief$getContainerUUID();
}
