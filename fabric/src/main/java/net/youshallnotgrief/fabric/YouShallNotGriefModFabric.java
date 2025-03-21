package net.youshallnotgrief.fabric;

import net.minecraftforge.fml.config.ModConfig;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

public class YouShallNotGriefModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YouShallNotGriefMod.init();
        ForgeConfigRegistry.INSTANCE.register(
                YouShallNotGriefMod.MOD_ID, ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
