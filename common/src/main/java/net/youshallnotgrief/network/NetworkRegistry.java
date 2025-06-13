package net.youshallnotgrief.network;

import dev.architectury.networking.NetworkManager;

public class NetworkRegistry {

    public static void registerClientToServerPackets() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S, InspectPacket.TYPE, InspectPacket.CODEC, InspectPacket::handle);
    }
}
