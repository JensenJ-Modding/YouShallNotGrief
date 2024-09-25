package net.youshallnotgrief.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.youshallnotgrief.YouShallNotGriefMod;

@Mod(YouShallNotGriefMod.MOD_ID)
public class YouShallNotGriefModForge {
    public YouShallNotGriefModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(YouShallNotGriefMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        YouShallNotGriefMod.init();
    }
}
