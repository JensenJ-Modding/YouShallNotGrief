package net.youshallnotgrief.util;

import net.minecraft.world.level.Level;

public class MiscUtils {

    public static String getDimensionIDFromLevel(Level level){
        return level.dimension().location().toString();
    }
}
