package net.youshallnotsteal.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.youshallnotsteal.YouShallNotStealMod;

@Mod(YouShallNotStealMod.MOD_ID)
public class YouShallNotStealModForge {
    public YouShallNotStealModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(YouShallNotStealMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        YouShallNotStealMod.init();
    }
}
