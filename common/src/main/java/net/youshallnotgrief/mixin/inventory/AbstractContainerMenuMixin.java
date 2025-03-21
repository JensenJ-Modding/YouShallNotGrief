package net.youshallnotgrief.mixin.inventory;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.youshallnotgrief.util.InventoryUtils;
import net.youshallnotgrief.util.mixin.MenuContext;
import net.youshallnotgrief.util.mixin.SlotMenuContext;
import org.jetbrains.annotations.NotNull;

@Mixin(value = AbstractContainerMenu.class, priority = 10100)
public abstract class AbstractContainerMenuMixin implements MenuContext {

    @Unique ServerPlayer youshallnotgrief$player;

    @Inject(method = "addSlot", at = @At("HEAD"))
    private void youshallnotgrief$addSlotToContext(Slot slot, CallbackInfoReturnable<Slot> cir) {
        ((SlotMenuContext) slot).youshallnotgrief$setContainer((AbstractContainerMenu) (Object) this);
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void youshallnotgrief$capturePlayer1(Player player, int i, CallbackInfoReturnable<Boolean> cir) {
        if (player.level().isClientSide()) {
            return;
        }
        youshallnotgrief$player = (ServerPlayer) player;
    }

    @Inject(method = "doClick", at = @At("HEAD"))
    private void youshallnotgrief$capturePlayer2(int i, int j, ClickType clickType, Player player, CallbackInfo ci) {
        if (player.level().isClientSide()) {
            return;
        }
        youshallnotgrief$player = (ServerPlayer) player;
    }

    @Inject(method = "clearContainer", at = @At("HEAD"))
    private void youshallnotgrief$capturePlayer3(Player player, Container container, CallbackInfo ci) {
        if (player.level().isClientSide()) {
            return;
        }
        youshallnotgrief$player = (ServerPlayer) player;
    }

    @Override
    public ServerPlayer youshallnotgrief$getPlayer() {
        return youshallnotgrief$player;
    }

    @Override
    public void youshallnotgrief$onStackChangedInEntity(ItemStack oldStack, ItemStack newStack, UUID entity) {
        Level level = youshallnotgrief$player.level();
        youshallnotgrief$onStackChanged(
                oldStack,
                newStack,
                (addedStack) -> {
                    InventoryUtils.addToDatabase(
                            entity, level, addedStack.getItem(), addedStack.getCount(), youshallnotgrief$player, "");
                },
                (removedStack) -> {
                    InventoryUtils.addToDatabase(
                            entity,
                            level,
                            removedStack.getItem(),
                            -removedStack.getCount(),
                            youshallnotgrief$player,
                            "");
                });
    }

    @Override
    public void youshallnotgrief$onStackChangedInBlock(
            @NotNull ItemStack oldStack, @NotNull ItemStack newStack, @NotNull BlockPos pos) {
        Level level = youshallnotgrief$player.level();
        youshallnotgrief$onStackChanged(
                oldStack,
                newStack,
                (addedStack) -> {
                    InventoryUtils.addToDatabase(
                            pos, level, addedStack.getItem(), addedStack.getCount(), youshallnotgrief$player, "");
                },
                (removedStack) -> {
                    InventoryUtils.addToDatabase(
                            pos, level, removedStack.getItem(), -removedStack.getCount(), youshallnotgrief$player, "");
                });
    }

    @Unique private void youshallnotgrief$onStackChanged(
            @NotNull ItemStack oldStack,
            @NotNull ItemStack newStack,
            Consumer<ItemStack> onItemAdded,
            Consumer<ItemStack> onItemRemoved) {
        if (oldStack.isEmpty() && newStack.isEmpty()) {
            return;
        }

        if (!oldStack.isEmpty() && !newStack.isEmpty()) { // 2 non-empty stacks, so either a swap, grow or shrink
            if (oldStack.getItem()
                    == newStack.getItem()) { // If the items in the stack are the same, then we add or remove an amount
                int newCount = newStack.getCount();
                int oldCount = oldStack.getCount();
                if (newCount > oldCount) {
                    ItemStack addedStack = new ItemStack(newStack.getItem(), newCount - oldCount);
                    onItemAdded.accept(addedStack);
                    if (addedStack.isEmpty()) {
                        return;
                    }
                } else {
                    ItemStack removedStack = new ItemStack(newStack.getItem(), oldCount - newCount);
                    if (removedStack.isEmpty()) {
                        return;
                    }
                    onItemRemoved.accept(removedStack);
                }
            } else { // Split up removing and adding items if we need to swap them.
                youshallnotgrief$onStackChanged(oldStack, ItemStack.EMPTY, onItemAdded, onItemRemoved);
                youshallnotgrief$onStackChanged(ItemStack.EMPTY, newStack, onItemAdded, onItemRemoved);
            }
            return;
        }

        if (oldStack.isEmpty()) {
            onItemAdded.accept(newStack);
        } else {
            onItemRemoved.accept(oldStack);
        }
    }
}
