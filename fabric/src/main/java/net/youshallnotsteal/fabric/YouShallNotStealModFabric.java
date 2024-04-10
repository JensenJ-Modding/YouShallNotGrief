package net.youshallnotsteal.fabric;

import net.fabricmc.api.ModInitializer;
import net.youshallnotsteal.YouShallNotStealMod;

public class YouShallNotStealModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YouShallNotStealMod.init();
    }
}
