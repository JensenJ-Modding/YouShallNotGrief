package net.youshallnotgrief.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.util.InspectionMode;

import static net.youshallnotgrief.util.InspectionMode.getTextColourFromConfig;

public class NetworkRegistry {
    public static final ResourceLocation INSPECT_PACKET_ID = new ResourceLocation(YouShallNotGriefMod.MOD_ID, "inspect_packet");

    public static void registerClientToServerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, INSPECT_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            //TODO: replace hardcoded level 2 with something better
            if(player.hasPermissions(2)) {
                InspectionMode.toggleInspectMode(context.getPlayer());
            }else{
                player.sendSystemMessage(Component.translatable("error.youshallnotgrief.inspection.nopermission").withStyle(style -> style
                        .withColor(getTextColourFromConfig(ServerConfig.inspectionErrorColour.get()))));
            }
        });
    }
}
