package net.youshallnotgrief.data.block.cause;

import net.youshallnotgrief.YouShallNotGriefMod;

import java.util.HashMap;
import java.util.Map;

public class BlockSetCauses {
    private static final Map<String, BlockSetCause> CAUSES = new HashMap<>();

    public static BlockSetCause GRAVITY = registerCause(new GravityCause());
    public static BlockSetCause LAND = registerCause(new LandCause());
    public static BlockSetCause FIRE = registerCause(new FireCause());
    public static BlockSetCause SPREAD = registerCause(new SpreadCause());
    public static BlockSetCause MELT = registerCause(new MeltCause());
    public static BlockSetCause DECAY = registerCause(new DecayCause());
    public static BlockSetCause GROW = registerCause(new GrowCause());
    public static BlockSetCause EXTINGUISH = registerCause(new ExtinguishCause());
    public static BlockSetCause PORTAL = registerCause(new PortalCause());
    public static BlockSetCause LIT = registerCause(new LitCause());
    public static BlockSetCause USED = registerCause(new UsedCause());
    public static BlockSetCause SCRAPED = registerCause(new ScrapedCause());
    public static BlockSetCause UNSUPPORTED = registerCause(new UnsupportedCause());
    public static BlockSetCause GOLEM_CREATION = registerCause(new GolemCreationCause());
    public static BlockSetCause REMOVED = registerCause(new RemovedCause());
    public static BlockSetCause PLACED = registerCause(new PlacedCause());
    public static BlockSetCause TRAMPLED = registerCause(new TrampledCause());
    public static BlockSetCause OPENED = registerCause(new OpenedCause());
    public static BlockSetCause CLOSED = registerCause(new ClosedCause());
    public static BlockSetCause EVAPORATION = registerCause(new EvaporationCause());
    public static BlockSetCause EXPLOSION = registerCause(new ExplosionCause());
    public static BlockSetCause ATE = registerCause(new AteCause());
    public static BlockSetCause FROST_WALKER = registerCause(new FrostWalkerCause());
    public static BlockSetCause WAXED = registerCause(new WaxedCause());
    public static BlockSetCause PAVED = registerCause(new PavedCause());
    public static BlockSetCause PLOUGHED = registerCause(new PloughedCause());
    public static BlockSetCause DAMAGED = registerCause(new DamagedCause());
    public static BlockSetCause CHANGED = registerCause(new ChangedCause());
    public static BlockSetCause HATCHED = registerCause(new HatchedCause());
    public static BlockSetCause MODDED = registerCause(new ModdedCause());

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
