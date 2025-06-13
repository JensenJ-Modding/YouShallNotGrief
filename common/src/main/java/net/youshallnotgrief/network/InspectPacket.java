package net.youshallnotgrief.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import dev.architectury.networking.NetworkManager;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.inspection.InspectionMode;
import net.youshallnotgrief.util.MiscUtils;
import org.jetbrains.annotations.NotNull;

public class InspectPacket implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(YouShallNotGriefMod.MOD_ID, "inspect_packet");
    public static final CustomPacketPayload.Type<InspectPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, InspectPacket> CODEC =
            CustomPacketPayload.codec(InspectPacket::write, InspectPacket::new);

    public InspectPacket(RegistryFriendlyByteBuf buffer) {}

    public void write(RegistryFriendlyByteBuf buffer) {}

    public static void handle(InspectPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        if (player.hasPermissions(ServerConfig.inspectionOpNeeded.get() ? 1 : 0)) {
            InspectionMode.toggleInspectMode(player);
        } else {
            player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.nopermission")
                    .withStyle(style -> style.withColor(
                            MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
