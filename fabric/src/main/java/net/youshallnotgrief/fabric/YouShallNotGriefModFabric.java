package net.youshallnotgrief.fabric;

import net.fabricmc.api.ModInitializer;
import net.youshallnotgrief.YouShallNotGriefMod;

public class YouShallNotGriefModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YouShallNotGriefMod.init();
    }
}
