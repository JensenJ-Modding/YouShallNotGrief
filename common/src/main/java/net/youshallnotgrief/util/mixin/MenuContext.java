package net.youshallnotgrief.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

//This is used to act as a bridge between the menu, slots and the player
public interface MenuContext {
    ServerPlayer youshallnotgrief$getPlayer();
    void youshallnotgrief$onStackChangedInBlock(ItemStack oldStack, ItemStack newStack, BlockPos position);
    void youshallnotgrief$onStackChangedInEntity(ItemStack oldStack, ItemStack newStack, UUID entity);
}
