package net.youshallnotgrief.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.util.InspectionMode;
import net.youshallnotgrief.util.MiscUtils;

public class NetworkRegistry {
    public static final ResourceLocation INSPECT_PACKET_ID = new ResourceLocation(YouShallNotGriefMod.MOD_ID, "inspect_packet");

    public static void registerClientToServerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, INSPECT_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            if(player.hasPermissions(ServerConfig.inspectionOpNeeded.get() ? 1 : 0)) {
                InspectionMode.toggleInspectMode(context.getPlayer());
            }else{
                player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.nopermission").withStyle(style -> style
                        .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
            }
        });
    }
}
