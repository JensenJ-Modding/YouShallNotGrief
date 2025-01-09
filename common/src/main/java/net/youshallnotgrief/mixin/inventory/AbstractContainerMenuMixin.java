package net.youshallnotgrief.mixin.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.InventoryUtils;
import net.youshallnotgrief.util.mixin.MenuContext;
import net.youshallnotgrief.util.mixin.SlotMenuContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = AbstractContainerMenu.class, priority = 10100)
public abstract class AbstractContainerMenuMixin implements MenuContext {

    @Unique
    ServerPlayer youshallnotgrief$player;

    @Inject(method = "addSlot", at = @At("HEAD"))
    private void youshallnotgrief$addSlotToContext(Slot slot, CallbackInfoReturnable<Slot> cir){
        ((SlotMenuContext) slot).youshallnotgrief$setContainer((AbstractContainerMenu) (Object) this);
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void youshallnotgrief$capturePlayer1(Player player, int i, CallbackInfoReturnable<Boolean> cir){
        if(player.level().isClientSide()) { return; }
        youshallnotgrief$player = (ServerPlayer) player;
    }

    @Inject(method = "doClick", at = @At("HEAD"))
    private void youshallnotgrief$capturePlayer2(int i, int j, ClickType clickType, Player player, CallbackInfo ci){
        if(player.level().isClientSide()) { return; }
        youshallnotgrief$player = (ServerPlayer) player;
    }

    @Inject(method = "clearContainer", at = @At("HEAD"))
    private void youshallnotgrief$capturePlayer3(Player player, Container container, CallbackInfo ci){
        if(player.level().isClientSide()) { return; }
        youshallnotgrief$player = (ServerPlayer) player;
    }

    @Override
    public ServerPlayer youshallnotgrief$getPlayer() {
        return youshallnotgrief$player;
    }

    @Override
    public void youshallnotgrief$onStackChangedInEntity(ItemStack oldStack, ItemStack newStack, UUID entity) {
        ItemStack changedStack = youshallnotgrief$onStackChanged(oldStack, newStack);
        if(changedStack == null) {
            return;
        }

        if (oldStack.isEmpty()) {
            InventoryUtils.addToDatabase(entity, changedStack.getItem(), changedStack.getCount(), youshallnotgrief$player, "");
            YouShallNotGriefMod.LOGGER.info("{} has now added {} to {}", youshallnotgrief$player, changedStack, entity);
        } else {
            InventoryUtils.addToDatabase(entity, changedStack.getItem(), -changedStack.getCount(), youshallnotgrief$player, "");
            YouShallNotGriefMod.LOGGER.info("{} has now removed {} from {}", youshallnotgrief$player, changedStack, entity);

        }
    }

    @Override
    public void youshallnotgrief$onStackChangedInBlock(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, @NotNull BlockPos pos) {
        ItemStack changedStack = youshallnotgrief$onStackChanged(oldStack, newStack);
        if(changedStack == null) {
            return;
        }

        Level level = youshallnotgrief$player.level();
        if (oldStack.isEmpty()) {
            InventoryUtils.addToDatabase(pos, level, changedStack.getItem(), changedStack.getCount(), youshallnotgrief$player, "");
            YouShallNotGriefMod.LOGGER.info("{} has now added {} to {}", youshallnotgrief$player, changedStack, pos);
        } else {
            InventoryUtils.addToDatabase(pos, level, changedStack.getItem(), -changedStack.getCount(), youshallnotgrief$player, "");
            YouShallNotGriefMod.LOGGER.info("{} has now removed {} from {}", youshallnotgrief$player, changedStack, pos);
        }
    }

    @Unique
    private ItemStack youshallnotgrief$onStackChanged(@NotNull ItemStack oldStack, @NotNull ItemStack newStack){
        if (oldStack.isEmpty() && newStack.isEmpty()) {
            return null;
        }

        //TODO: something here does not work, need to fix
        if (!oldStack.isEmpty() && !newStack.isEmpty()) { // 2 non-empty stacks
            if (oldStack.getItem() == newStack.getItem()) { // If the items in the stack are the same, then we add or remove an amount
                int newCount = newStack.getCount();
                int oldCount = oldStack.getCount();
                if (newCount > oldCount) {
                    youshallnotgrief$onStackChanged(ItemStack.EMPTY, new ItemStack(newStack.getItem(), newCount - oldCount));
                } else {
                    youshallnotgrief$onStackChanged(new ItemStack(newStack.getItem(), oldCount - newCount), ItemStack.EMPTY);
                }
            } else { //Split up removing and adding items if we need to swap them.
                youshallnotgrief$onStackChanged(oldStack, ItemStack.EMPTY);
                youshallnotgrief$onStackChanged(ItemStack.EMPTY, newStack);
            }
            return null;
        }

        //Return the itemstack which was added or removed
        return oldStack.isEmpty() ? newStack : oldStack;
    }
}
