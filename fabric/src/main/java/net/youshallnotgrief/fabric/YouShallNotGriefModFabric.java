package net.youshallnotgrief.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

public class YouShallNotGriefModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YouShallNotGriefMod.init();
        NeoForgeConfigRegistry.INSTANCE.register(
                YouShallNotGriefMod.MOD_ID, ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
