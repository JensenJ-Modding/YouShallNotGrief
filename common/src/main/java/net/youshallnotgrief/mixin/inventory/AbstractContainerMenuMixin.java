package net.youshallnotgrief.mixin.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.mixin.PlayerMenuContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractContainerMenu.class, priority = 10100)
public class AbstractContainerMenuMixin {

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    private void youshallnotgrief$wrapOnTake1(Slot slot, Player player, ItemStack itemStack, Operation<Void> original){
        //When a player clicks a block which has an inventory map player to position

        if(!player.level().isClientSide()){
            YouShallNotGriefMod.LOGGER.info("1: {} took {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
        original.call(slot, player, itemStack);
    }


    @WrapOperation(method = "method_34249", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    private void youshallnotgrief$wrapOnTake2(Slot slot, Player player, ItemStack itemStack, Operation<Void> original){
        //When a player clicks a block which has an inventory map player to position
        if(!player.level().isClientSide()) {
            YouShallNotGriefMod.LOGGER.info("2: {} took {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
        original.call(slot, player, itemStack);
    }

    @WrapOperation(method = "method_34251", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void youshallnotgrief$wrapOnTake3(Slot slot, Player player, ItemStack itemStack, Operation<Void> original){
        //When a player clicks a block which has an inventory map player to position
        if(!player.level().isClientSide()) {
            YouShallNotGriefMod.LOGGER.info("3: {} took {} at {}", player, itemStack, ((PlayerMenuContext) player).youshallnotgrief$getContainerPos());
        }
        original.call(slot, player, itemStack);
    }
}
