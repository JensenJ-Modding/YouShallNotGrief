package net.youshallnotgrief.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

@Mod(YouShallNotGriefMod.MOD_ID)
public class YouShallNotGriefModForge {
    public YouShallNotGriefModForge() {
        EventBuses.registerModEventBus(YouShallNotGriefMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        YouShallNotGriefMod.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
