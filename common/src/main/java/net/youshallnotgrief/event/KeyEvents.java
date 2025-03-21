package net.youshallnotgrief.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.netty.buffer.Unpooled;
import net.youshallnotgrief.network.NetworkRegistry;

public class KeyEvents {

    public static final KeyMapping KEYMAPPING_INSPECT = new KeyMapping(
            "key.youshallnotgrief.inspect",
            InputConstants.Type.KEYSYM,
            -1, // The default keycode (bound to none)
            "category.youshallnotgrief");

    public static void registerEvents() {
        KeyMappingRegistry.register(KEYMAPPING_INSPECT);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (KEYMAPPING_INSPECT.consumeClick()) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                NetworkManager.sendToServer(NetworkRegistry.INSPECT_PACKET_ID, buf);
            }
        });
    }
}
