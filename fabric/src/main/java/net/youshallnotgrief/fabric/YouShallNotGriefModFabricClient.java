package net.youshallnotgrief.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.youshallnotgrief.YouShallNotGriefMod;

public class YouShallNotGriefModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        YouShallNotGriefMod.initClient();
    }
}
