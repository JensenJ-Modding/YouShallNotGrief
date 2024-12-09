package net.youshallnotgrief.data.block.cause;

import net.youshallnotgrief.YouShallNotGriefMod;

import java.util.HashMap;
import java.util.Map;

public class BlockSetCauses {
    private static final Map<String, BlockSetCause> CAUSES = new HashMap<>();

    public static BlockSetCause GRAVITY = registerCause(new BlockSetCause("gravity"));
    public static BlockSetCause LAND = registerCause(new BlockSetCause("land"));
    public static BlockSetCause FIRE = registerCause(new BlockSetCause("fire"));
    public static BlockSetCause SPREAD = registerCause(new BlockSetCause("spread"));
    public static BlockSetCause MELT = registerCause(new BlockSetCause("melt"));
    public static BlockSetCause DECAY = registerCause(new BlockSetCause("decay"));
    public static BlockSetCause GROW = registerCause(new BlockSetCause("grow"));
    public static BlockSetCause EXTINGUISH = registerCause(new BlockSetCauseWithSourceFallback("extinguish"));
    public static BlockSetCause PORTAL = registerCause(new BlockSetCause("portal"));
    public static BlockSetCause LIT = registerCause(new BlockSetCauseWithSourceFallback("lit"));
    public static BlockSetCause USED = registerCause(new BlockSetCause("used"));
    public static BlockSetCause SCRAPED = registerCause(new BlockSetCause("scraped"));
    public static BlockSetCause UNSUPPORTED = registerCause(new BlockSetCause("unsupported"));
    public static BlockSetCause GOLEM_CREATION = registerCause(new BlockSetCause("golemcreation"));
    public static BlockSetCause REMOVED = registerCause(new BlockSetCause("removed"));
    public static BlockSetCause PLACED = registerCause(new BlockSetCause("placed"));
    public static BlockSetCause TRAMPLED = registerCause(new BlockSetCause("trampled"));
    public static BlockSetCause OPENED = registerCause(new BlockSetCause("opened"));
    public static BlockSetCause CLOSED = registerCause(new BlockSetCause("closed"));
    public static BlockSetCause EVAPORATION = registerCause(new BlockSetCause("evaporation"));
    public static BlockSetCause EXPLOSION = registerCause(new BlockSetCause("explosion"));
    public static BlockSetCause ATE = registerCause(new BlockSetCause("ate"));
    public static BlockSetCause FROST_WALKER = registerCause(new BlockSetCause("frostwalker"));
    public static BlockSetCause WAXED = registerCause(new BlockSetCause("waxed"));
    public static BlockSetCause PAVED = registerCause(new BlockSetCause("paved"));
    public static BlockSetCause PLOUGHED = registerCause(new BlockSetCause("ploughed"));
    public static BlockSetCause DAMAGED = registerCause(new BlockSetCause("damaged"));
    public static BlockSetCause CHANGED = registerCause(new BlockSetCause("changed"));
    public static BlockSetCause HATCHED = registerCause(new BlockSetCause("hatched"));
    public static BlockSetCause INFESTED = registerCause(new BlockSetCause("infested"));
    public static BlockSetCause MODDED = registerCause(new BlockSetCause("modded"));

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
