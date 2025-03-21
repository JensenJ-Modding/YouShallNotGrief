package net.youshallnotgrief.database.data.cause;

import java.util.HashMap;
import java.util.Map;

import net.youshallnotgrief.YouShallNotGriefMod;

public class BlockSetCauses {
    private static final Map<String, BlockSetCause> CAUSES = new HashMap<>();

    public static final BlockSetCause GRAVITY = registerCause(new BlockSetCause("gravity"));
    public static final BlockSetCause LAND = registerCause(new BlockSetCause("land"));
    public static final BlockSetCause FIRE = registerCause(new BlockSetCause("fire"));
    public static final BlockSetCause SPREAD = registerCause(new BlockSetCause("spread"));
    public static final BlockSetCause MELT = registerCause(new BlockSetCause("melt"));
    public static final BlockSetCause OXIDIZATION = registerCause(new BlockSetCause("oxidization"));
    public static final BlockSetCause DECAY = registerCause(new BlockSetCause("decay"));
    public static final BlockSetCause GROW = registerCause(new BlockSetCause("grow"));
    public static final BlockSetCause EXTINGUISH = registerCause(new BlockSetCauseWithSourceFallback("extinguish"));
    public static final BlockSetCause PORTAL = registerCause(new BlockSetCause("portal"));
    public static final BlockSetCause LIT = registerCause(new BlockSetCauseWithSourceFallback("lit"));
    public static final BlockSetCause USED = registerCause(new BlockSetCause("used"));
    public static final BlockSetCause SCRAPED = registerCause(new BlockSetCause("scraped"));
    public static final BlockSetCause UNSUPPORTED = registerCause(new BlockSetCause("unsupported"));
    public static final BlockSetCause GOLEM_CREATION = registerCause(new BlockSetCause("golemcreation"));
    public static final BlockSetCause REMOVED = registerCause(new BlockSetCause("removed"));
    public static final BlockSetCause PLACED = registerCause(new BlockSetCause("placed"));
    public static final BlockSetCause TRAMPLED = registerCause(new BlockSetCause("trampled"));
    public static final BlockSetCause OPENED = registerCause(new BlockSetCause("opened"));
    public static final BlockSetCause CLOSED = registerCause(new BlockSetCause("closed"));
    public static final BlockSetCause EVAPORATION = registerCause(new BlockSetCause("evaporation"));
    public static final BlockSetCause EXPLOSION = registerCause(new BlockSetCause("explosion"));
    public static final BlockSetCause ATE = registerCause(new BlockSetCause("ate"));
    public static final BlockSetCause FROST_WALKER = registerCause(new BlockSetCause("frostwalker"));
    public static final BlockSetCause WAXED = registerCause(new BlockSetCause("waxed"));
    public static final BlockSetCause PAVED = registerCause(new BlockSetCause("paved"));
    public static final BlockSetCause PLOUGHED = registerCause(new BlockSetCause("ploughed"));
    public static final BlockSetCause DAMAGED = registerCause(new BlockSetCause("damaged"));
    public static final BlockSetCause HATCHED = registerCause(new BlockSetCause("hatched"));
    public static final BlockSetCause INFESTED = registerCause(new BlockSetCause("infested"));
    public static final BlockSetCause ACCESSED = registerCause(new BlockSetCause("accessed"));
    public static final BlockSetCause INTERACTED = registerCause(new BlockSetCause("interacted"));
    public static final BlockSetCause FALLBACK = registerCause(new BlockSetCauseWithSourceFallback("fallback"));

    private static BlockSetCause registerCause(BlockSetCause cause) {
        if (CAUSES.containsKey(cause.getDatabaseTag())) {
            YouShallNotGriefMod.LOGGER.fatal("Error when initialising BlockSetCauses");
            throw new IllegalStateException(cause.getDatabaseTag()
                    + " already exists in the CAUSES map. It was most likely declared twice accidentally.");
        }
        CAUSES.put(cause.getDatabaseTag(), cause);
        return cause;
    }

    public static BlockSetCause getCauseFromTag(String tag) {
        return CAUSES.get(tag);
    }
}
