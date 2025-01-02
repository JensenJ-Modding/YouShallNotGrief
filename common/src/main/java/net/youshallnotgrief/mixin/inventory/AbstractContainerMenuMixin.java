package net.youshallnotgrief.mixin.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.mixin.PlayerMenuContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerMenu.class, priority = 10100)
public abstract class AbstractContainerMenuMixin {

    @Unique
    private Player youshallnotgrief$capturedPlayer;

    @Inject(method = "doClick", at = @At(value = "HEAD"))
    private void youshallnotgrief$capturePlayer(int i, int j, ClickType clickType, Player player, CallbackInfo ci){
        if(!player.level().isClientSide()) {
            youshallnotgrief$capturedPlayer = player;
        }
    }

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;safeInsert(Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack youshallnotgrief$wrapPlace(Slot slot, ItemStack itemStack, int i, Operation<ItemStack> original){
        ItemStack originalStack = itemStack.copy();
        ItemStack result = original.call(slot, itemStack, i);
        Player player = youshallnotgrief$capturedPlayer;
        if(youshallnotgrief$guardInventoryTransaction(player)){
            if(slot.container instanceof Inventory) {
                return result;
            }

            //If the contents of the slot didn't change, don't log anything
            if(ItemStack.matches(originalStack, result)){
                return result;
            }

            ItemStack placed = new ItemStack(itemStack.getItem(), originalStack.getCount() - result.getCount());
            if(placed.getCount() == 0){
                placed = originalStack;
            }
            YouShallNotGriefMod.LOGGER.info("1: {} placed {} at {}", player, placed, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
        return result;
    }

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void youshallnotgrief$wrapSwapTake(AbstractContainerMenu menu, ItemStack stack, Operation<Void> original, @Local Slot slot){
        original.call(menu, stack);
        Player player = youshallnotgrief$capturedPlayer;
        if(youshallnotgrief$guardInventoryTransaction(player)){
            if(slot.container instanceof Inventory) {
                return;
            }

            if(stack.getCount() == 0){
                return;
            }
            YouShallNotGriefMod.LOGGER.info("2: {} took {} at {}", player, stack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
    }

    @WrapOperation(method = "method_34249", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    private void youshallnotgrief$wrapTake(Slot slot, Player player, ItemStack itemStack, Operation<Void> original){
        original.call(slot, player, itemStack);
        if(youshallnotgrief$guardInventoryTransaction(player)){
            if(slot.container instanceof Inventory){
                return;
            }
            YouShallNotGriefMod.LOGGER.info("3: {} took {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
    }

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setByPlayer(Lnet/minecraft/world/item/ItemStack;)V"))
    private void youshallnotgrief$wrapMergeStack(Slot slot, ItemStack itemStack, Operation<Void> original, @Local(argsOnly = true) Player player){
        ItemStack originalStack = slot.getItem().copy();
        original.call(slot, itemStack);
        if(youshallnotgrief$guardInventoryTransaction(player)){
            if(slot.container instanceof Inventory){
                return;
            }

            ItemStack placed = itemStack;
            if(ItemStack.isSameItemSameTags(originalStack, itemStack)) {
                //If the stack was not added to by quick crafting
                if (originalStack.getCount() == itemStack.getCount()) {
                    return;
                }
                placed = new ItemStack(originalStack.getItem(), itemStack.getCount() - originalStack.getCount());
                if(placed.getCount() == 0){
                    placed = itemStack;
                }
            }

            YouShallNotGriefMod.LOGGER.info("4: {} placed {} at {}", player, placed, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
    }

    @WrapOperation(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setByPlayer(Lnet/minecraft/world/item/ItemStack;)V"))
    private void youshallnotgrief$wrapShiftClickNewStack(Slot slot, ItemStack itemStack, Operation<Void> original){
        original.call(slot, itemStack);
        Player player = youshallnotgrief$capturedPlayer;
        if(youshallnotgrief$guardInventoryTransaction(player)){
            //If the slot we are placing into is the player's inventory
            if(slot.container instanceof Inventory){
                YouShallNotGriefMod.LOGGER.info("5: {} took {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
            }else {
                YouShallNotGriefMod.LOGGER.info("5: {} placed {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
            }
        }
    }

    @WrapOperation(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V"))
    private void youshallnotgrief$wrapShiftClickPartialStack(ItemStack itemStack, int i, Operation<Void> original, @Local Slot slot){
        ItemStack originalStack = itemStack.copy();
        original.call(itemStack, i);
        Player player = youshallnotgrief$capturedPlayer;
        if(youshallnotgrief$guardInventoryTransaction(player)){
            if(slot.getItem() == itemStack) {
                ItemStack loggedStack = new ItemStack(originalStack.getItem(), itemStack.getCount() - originalStack.getCount());
                if(slot.container instanceof Inventory){
                    YouShallNotGriefMod.LOGGER.info("6: {} took {} at {}", player, loggedStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
                }else {
                    YouShallNotGriefMod.LOGGER.info("6: {} placed {} at {}", player, loggedStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
                }
            }
        }
    }

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;", ordinal = 3))
    private ItemEntity youshallnotgrief$wrapPlayerDropItemFromContainer(Player player, ItemStack itemStack, boolean bl, Operation<ItemEntity> original, @Local Slot slot){
        if(youshallnotgrief$guardInventoryTransaction(player)){
            if(!(slot.container instanceof Inventory)) {
                YouShallNotGriefMod.LOGGER.info("7: {} dropped {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
            }
        }
        return original.call(player, itemStack, bl);
    }

    //TODO: Make a second guard function which checks if the original itemStack is different from the new one in the targeted slot,
    // indicating whether any operation occurred.
    // This function should also check if config is enabled for manual player inventory tracking
    // and should be called before we log any changes

    @Unique
    private static boolean youshallnotgrief$guardInventoryTransaction(Player player){
        if(player == null){
            return false;
        }

        Level level = player.level();
        if(level.isClientSide()){
            return false;
        }

        return ((PlayerMenuContext) player).youshallnotgrief$getContainerPos() != null;
    }
}
