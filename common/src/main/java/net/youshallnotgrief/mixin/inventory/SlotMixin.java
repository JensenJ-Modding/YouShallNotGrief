package net.youshallnotgrief.mixin.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.youshallnotgrief.util.mixin.BlockPositionMenuContext;
import net.youshallnotgrief.util.mixin.EntityUUIDMenuContext;
import net.youshallnotgrief.util.mixin.MenuContext;
import net.youshallnotgrief.util.mixin.SlotMenuContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = Slot.class, priority = 10100)
public abstract class SlotMixin implements SlotMenuContext {

    @Shadow public abstract ItemStack getItem();

    @Shadow public int index;
    @Shadow @Final public Container container;
    @Unique
    private ItemStack youshallnotgrief$oldStack;

    @Unique
    private AbstractContainerMenu youshallnotgrief$menu;

    @Inject(method = "setChanged", at = @At(value = "HEAD"))
    private void youshallnotgrief$captureSlotChange(CallbackInfo ci){
        MenuContext context = ((MenuContext) youshallnotgrief$menu);
        ServerPlayer player = context.youshallnotgrief$getPlayer();
        if(container instanceof Inventory){
            return;
        }

        if(player != null) {
            if(player.level().isClientSide()) {
                return;
            }

            BlockPos pos = youshallnotgrief$getInventoryBlockPosition();
            if (pos != null) {
                context.youshallnotgrief$onStackChangedInBlock(youshallnotgrief$oldStack, getItem().copy(), pos);
            }
            //If the position is null, this could be an entity slot interaction, such as a minecart, boat, horse etc.
            else {
                UUID entityID = youshallnotgrief$getInventoryEntityUUID();
                if(entityID != null){
                    context.youshallnotgrief$onStackChangedInEntity(youshallnotgrief$oldStack, getItem().copy(), entityID);
                }
            }

            youshallnotgrief$oldStack = getItem().copy();
        }
    }

    @Unique
    @Nullable
    private BlockPos youshallnotgrief$getInventoryBlockPosition(){
        ServerPlayer player = ((MenuContext) youshallnotgrief$menu).youshallnotgrief$getPlayer();
        if(player == null) { return null; }
        return ((BlockPositionMenuContext) player).youshallnotgrief$getContainerPos();
    }

    @Unique
    @Nullable
    private UUID youshallnotgrief$getInventoryEntityUUID(){
        ServerPlayer player = ((MenuContext) youshallnotgrief$menu).youshallnotgrief$getPlayer();
        if(player == null) { return null; }
        return ((EntityUUIDMenuContext) player).youshallnotgrief$getContainerUUID();
    }

    @Override
    public void youshallnotgrief$setContainer(AbstractContainerMenu menu) {
        youshallnotgrief$menu = menu;
        youshallnotgrief$oldStack = getItem() != null ? getItem().copy() : ItemStack.EMPTY;
    }
}
