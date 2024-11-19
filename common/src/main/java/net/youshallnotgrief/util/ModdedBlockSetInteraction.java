package net.youshallnotgrief.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;

public class ModdedBlockSetInteraction {
    static HashSet<String> blacklistedModules = new HashSet<>() {{
        add("minecraft");
        add("fabricmc");
        add("forge");
        add("neoforge");
        add("google");
        add("unimi");
    }};

    static HashMap<String, String> stackPathToModID = new HashMap<>();

    //TODO: Redo this to be more intelligent
    public static void logModdedSetBlockInteractions(BlockPos blockPos, BlockState blockState, Level level) {
        if(level.isClientSide){
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int causeTraceIndex;
        for (causeTraceIndex = 2; causeTraceIndex < stackTraceElements.length - 1; causeTraceIndex++) {
            if(containsAny(stackTraceElements[causeTraceIndex].toString())){
                causeTraceIndex++;
            }else {
                break;
            }
        }

        //Did not find any modded interactions
        if(causeTraceIndex >= stackTraceElements.length){
            return;
        }

        StackTraceElement causeElement = stackTraceElements[causeTraceIndex];
        String methodName = causeElement.getMethodName();
        String className = causeElement.getClassName();

        String moduleName = "@" + getModIDFromClassName(className);
        if(moduleName.contains("java")){
            return;
        }

        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        className = className.substring(className.lastIndexOf(".") + 1);
        String fullName = className + ":" + methodName;

        BlockUtils.addToDatabase(blockPos, level, level.getBlockState(blockPos), blockState, moduleName, fullName);
    }


    @Unique
    private static boolean containsAny(String stackTraceModule) {
        for (String blacklistedModule : blacklistedModules) {
            if (stackTraceModule.contains(blacklistedModule)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static String getModIDFromClassName(String className){
        String[] elementParts = className.split("\\.");
        String moduleName = elementParts[0] + "." + elementParts[1] + "." + elementParts[2];

        if(stackPathToModID.containsKey(moduleName)){
            return stackPathToModID.get(moduleName);
        }

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            String modID = modContainer.getMetadata().getId();
            if(moduleName.contains(modID)){
                stackPathToModID.put(moduleName, modID);
            }
        });

        if(stackPathToModID.containsKey(moduleName)){
            return stackPathToModID.get(moduleName);
        }

        return moduleName;
    }
}
