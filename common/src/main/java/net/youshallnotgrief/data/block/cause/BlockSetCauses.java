package net.youshallnotgrief.data.block.cause;

import net.youshallnotgrief.YouShallNotGriefMod;

import java.util.HashMap;
import java.util.Map;

public class BlockSetCauses {
    private static final Map<String, BlockSetCause> CAUSES = new HashMap<>();

    public static BlockSetCause GRAVITY = registerCause(new GravityCause());
    public static BlockSetCause LAND = registerCause(new LandCause());
    public static BlockSetCause FIRE = registerCause(new FireCause());
    public static BlockSetCause MELT = registerCause(new MeltCause());
    public static BlockSetCause DECAY = registerCause(new DecayCause());
    public static BlockSetCause GROW = registerCause(new GrowCause());
    public static BlockSetCause PORTAL = registerCause(new PortalCause());
    public static BlockSetCause RAVAGER = registerCause(new RavagerCause());
    public static BlockSetCause SNOW_GOLEM = registerCause(new SnowGolemCause());

    private static BlockSetCause registerCause(BlockSetCause cause){
        if(CAUSES.containsKey(cause.getDatabaseTag())){
            YouShallNotGriefMod.LOGGER.fatal("Error when initialising BlockSetCauses");
            throw new IllegalStateException(cause.getDatabaseTag() + " already exists in the CAUSES map. It was most likely declared twice accidentally.");
        }
        CAUSES.put(cause.getDatabaseTag(), cause);
        return cause;
    }

    public static BlockSetCause getCauseFromTag(String tag){
        return CAUSES.get(tag);
    }
}
