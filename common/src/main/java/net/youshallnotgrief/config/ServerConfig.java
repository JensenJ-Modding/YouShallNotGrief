package net.youshallnotgrief.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static ForgeConfigSpec SERVER_CONFIG;

    private static final String CATEGORY_DATABASE = "database";
    private static final String CATEGORY_INSPECTION = "inspection";
    private static final String CATEGORY_DEBUG = "debug";
    private static final String CATEGORY_LOGGING = "logging";
    private static final String CATEGORY_BLOCK = "block";
    private static final String CATEGORY_PLAYER = "player";
    private static final String CATEGORY_MOB = "mobs";
    private static final String CATEGORY_INTERACTION = "interaction";

    //Database settings
    public static ForgeConfigSpec.ConfigValue<Integer> databaseThreadCount;
    public static ForgeConfigSpec.ConfigValue<Integer> databaseQueueSize;

    //Inspection settings
    public static ForgeConfigSpec.ConfigValue<String> inspectionPrimaryColour;
    public static ForgeConfigSpec.ConfigValue<String> inspectionSecondaryColour;
    public static ForgeConfigSpec.ConfigValue<String> inspectionBackgroundColour;
    public static ForgeConfigSpec.ConfigValue<String> inspectionErrorColour;
    public static ForgeConfigSpec.ConfigValue<Integer> inspectionTimePrecision;
    public static ForgeConfigSpec.ConfigValue<String> inspectionFullTimeFormat;
    public static ForgeConfigSpec.ConfigValue<Boolean> inspectionOpNeeded;

    //Debug settings
    public static ForgeConfigSpec.ConfigValue<Boolean> debugLogUnhandledBlockSets;
    public static ForgeConfigSpec.ConfigValue<Boolean> debugLogMultipleTimes;

    //Player block logging
    public static ForgeConfigSpec.ConfigValue<Boolean> logBlockPlacement;
    public static ForgeConfigSpec.ConfigValue<Boolean> logBlockBreaking;

    //Mob block logging
    public static ForgeConfigSpec.ConfigValue<Boolean> logBlockTrampling;
    public static ForgeConfigSpec.ConfigValue<Boolean> logMobHatching;
    public static ForgeConfigSpec.ConfigValue<Boolean> logMobInfestingBlock;
    public static ForgeConfigSpec.ConfigValue<Boolean> logMobDoorBreak;
    public static ForgeConfigSpec.ConfigValue<Boolean> logMobOpenDoor;
    public static ForgeConfigSpec.ConfigValue<Boolean> logMobEatBlock;
    public static ForgeConfigSpec.ConfigValue<Boolean> logEndermanGriefing;
    public static ForgeConfigSpec.ConfigValue<Boolean> logEnderDragonGriefing;
    public static ForgeConfigSpec.ConfigValue<Boolean> logRavagerGriefing;
    public static ForgeConfigSpec.ConfigValue<Boolean> logVillagerHarvesting;
    public static ForgeConfigSpec.ConfigValue<Boolean> logSnowGolemWalking;
    public static ForgeConfigSpec.ConfigValue<Boolean> logBlazeFireball;
    public static ForgeConfigSpec.ConfigValue<Boolean> logFrostWalker;

    //Non mob block logging
    public static ForgeConfigSpec.ConfigValue<Boolean> logEndFight;
    public static ForgeConfigSpec.ConfigValue<Boolean> logPlantGrowth;
    public static ForgeConfigSpec.ConfigValue<Boolean> logUnsupportedBlocks;
    public static ForgeConfigSpec.ConfigValue<Boolean> logAmethystGrowth;
    public static ForgeConfigSpec.ConfigValue<Boolean> logGolemCreation;
    public static ForgeConfigSpec.ConfigValue<Boolean> logFire;
    public static ForgeConfigSpec.ConfigValue<Boolean> logMelting;
    public static ForgeConfigSpec.ConfigValue<Boolean> logDecay;
    public static ForgeConfigSpec.ConfigValue<Boolean> logLightning;
    public static ForgeConfigSpec.ConfigValue<Boolean> logGravity;
    public static ForgeConfigSpec.ConfigValue<Boolean> logGrassSpread;
    public static ForgeConfigSpec.ConfigValue<Boolean> logPortals;
    public static ForgeConfigSpec.ConfigValue<Boolean> logExplosions;
    public static ForgeConfigSpec.ConfigValue<Boolean> logFallbackLevelSets;
    public static ForgeConfigSpec.ConfigValue<Boolean> logModdedLevelSets;

    //Player interaction logging
    public static ForgeConfigSpec.ConfigValue<Boolean> logFlintAndSteel;
    public static ForgeConfigSpec.ConfigValue<Boolean> logWaxing;
    public static ForgeConfigSpec.ConfigValue<Boolean> logScraping;
    public static ForgeConfigSpec.ConfigValue<Boolean> logPaving;
    public static ForgeConfigSpec.ConfigValue<Boolean> logExtinguish;
    public static ForgeConfigSpec.ConfigValue<Boolean> logPloughing;
    public static ForgeConfigSpec.ConfigValue<Boolean> logAnvilUse;
    public static ForgeConfigSpec.ConfigValue<Boolean> logDragonEggTeleportation;

    //Interaction logging
    public static ForgeConfigSpec.ConfigValue<Boolean> logProjectileLightBlock;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Database Settings").push(CATEGORY_DATABASE);
        databaseThreadCount = BUILDER.comment("Number of threads the mod should use at any one time. [Default: 2]")
                .defineInRange("databaseThreadCount", 2, 1, 8);
        databaseQueueSize = BUILDER.comment("Number of interactions the mod should queue before committing it into the database. [Default: 50]")
                .defineInRange("databaseQueueSize", 50, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Inspection mode settings").push(CATEGORY_INSPECTION);
        inspectionTimePrecision = BUILDER.comment("Number of decimal points to use when displaying time information [Default: 2]")
                .defineInRange("inspectionTimePrecision", 2, 1, 4);
        inspectionFullTimeFormat = BUILDER.comment("Full format of time to use when hovering over time value [Default: dd/MM/yyyy - HH:mm:ss]")
                .define("inspectionFullTimeFormat", "dd/MM/yyyy - HH:mm:ss");
        inspectionPrimaryColour = BUILDER.comment("Primary text colour used when in inspection mode [Default: #FFAA00]")
                .define("inspectionPrimaryColour", "#FFAA00");
        inspectionSecondaryColour = BUILDER.comment("Secondary text colour used when in inspection mode [Default: #BA9B5D]")
                .define("inspectionSecondaryColour", "#BA9B5D");
        inspectionBackgroundColour = BUILDER.comment("Background text colour used when in inspection mode [Default: #FFFFFF]")
                .define("inspectionBackgroundColour", "#FFFFFF");
        inspectionErrorColour = BUILDER.comment("Error text colour used when in inspection mode [Default: #ff5555]")
                .define("inspectionErrorColour", "#ff5555");
        inspectionOpNeeded = BUILDER.comment("Whether only server operators can use inspection mode and it's commands. [Default: true]")
                .define("inspectionOpNeeded", true);
        BUILDER.pop();

        BUILDER.comment("Dev/debug settings").push(CATEGORY_DEBUG);
        debugLogUnhandledBlockSets = BUILDER.comment("Should calls to Level.setBlock not wrapped by the mod be logged to console? [Default: false]")
                .define("debugLogUnhandledBlockSets", false);
        debugLogMultipleTimes = BUILDER.comment("Should console logs of the same type be logged beyond the first occurrence? [Default: false]")
                .define("debugLogMultipleTimes", false);
        BUILDER.pop();

        BUILDER.push(CATEGORY_LOGGING);
        BUILDER.comment("Block logging settings, by default everything is logged").push(CATEGORY_BLOCK);

        //Player block logging
        BUILDER.push(CATEGORY_PLAYER);
        logBlockPlacement = BUILDER.define("logBlockPlacement", true);
        logBlockBreaking = BUILDER.define("logBlockBreaking", true);

        BUILDER.pop();

        //Mob block logging
        BUILDER.push(CATEGORY_MOB);
        logEndFight = BUILDER.define("logEndFight", true);
        logBlockTrampling = BUILDER.define("logBlockTrampling", true);
        logMobHatching = BUILDER.define("logMobHatching", true);
        logMobInfestingBlock = BUILDER.define("logMobInfestingBlock", true);
        logMobDoorBreak = BUILDER.define("logMobDoorBreak", true);
        logMobEatBlock = BUILDER.define("logMobEatBlock", true);
        logEndermanGriefing = BUILDER.define("logEndermanGriefing", true);
        logEnderDragonGriefing = BUILDER.define("logEnderDragonGriefing", true);
        logRavagerGriefing = BUILDER.define("logRavagerGriefing", true);
        logVillagerHarvesting = BUILDER.define("logVillagerHarvesting", true);
        logSnowGolemWalking = BUILDER.define("logSnowGolemWalking", true);
        logBlazeFireball = BUILDER.define("logBlazeFireball", true);
        logFrostWalker = BUILDER.define("logFrostWalker", true);

        BUILDER.pop();

        //Non mob block logging
        logPlantGrowth = BUILDER.define("logPlantGrowth", true);
        logUnsupportedBlocks = BUILDER.define("logUnsupportedBlocks", true);
        logAmethystGrowth = BUILDER.define("logAmethystGrowth", true);
        logGolemCreation = BUILDER.define("logGolemCreation", true);
        logFire = BUILDER.define("logFire", true);
        logMelting = BUILDER.define("logMelting", true);
        logLightning = BUILDER.define("logLightning", true);
        logDecay = BUILDER.define("logDecay", true);
        logGravity = BUILDER.define("logGravity", true);
        logGrassSpread = BUILDER.define("logGrassSpread", true);
        logPortals = BUILDER.define("logPortals", true);
        logExplosions = BUILDER.define("logExplosions", true);
        logFallbackLevelSets = BUILDER.define("logFallbackLevelSets", true);
        logModdedLevelSets = BUILDER.define("logModdedLevelSets", true);
        BUILDER.pop();

        BUILDER.comment("Interaction logging settings, by default everything is logged.").push(CATEGORY_INTERACTION);
        BUILDER.push(CATEGORY_PLAYER);
        logFlintAndSteel = BUILDER.define("logFlintAndSteel", true);
        logWaxing = BUILDER.define("logWaxing", true);
        logScraping = BUILDER.define("logScraping", true);
        logPaving = BUILDER.define("logPaving", true);
        logExtinguish = BUILDER.define("logExtinguish", true);
        logPloughing = BUILDER.define("logPloughing", true);
        logAnvilUse = BUILDER.define("logAnvilUse", true);
        logDragonEggTeleportation = BUILDER.define("logDragonEggTeleportation", true);
        BUILDER.pop();
        logMobOpenDoor = BUILDER.define("logMobOpenDoor", true);
        logProjectileLightBlock = BUILDER.define("logProjectileLightBlock", true);
        BUILDER.pop();

        BUILDER.pop();

        SERVER_CONFIG = BUILDER.build();
    }
}
