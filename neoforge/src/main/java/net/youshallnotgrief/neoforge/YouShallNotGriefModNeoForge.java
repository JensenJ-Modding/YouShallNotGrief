package net.youshallnotgrief.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;

@Mod(YouShallNotGriefMod.MOD_ID)
public class YouShallNotGriefModNeoForge {
    public YouShallNotGriefModNeoForge(ModContainer container, IEventBus bus) {
        YouShallNotGriefMod.init();
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
