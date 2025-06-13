package net.youshallnotgrief.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.youshallnotgrief.YouShallNotGriefMod;

@Mod(value = YouShallNotGriefMod.MOD_ID, dist = Dist.CLIENT)
public class YouShallNotGriefModNeoForgeClient {

    public YouShallNotGriefModNeoForgeClient(IEventBus bus) {
        YouShallNotGriefMod.initClient();
    }
}
