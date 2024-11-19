package net.youshallnotgrief.mixin;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;

@Mixin(value = Level.class, priority = 10100)
public class LevelMixin {

    @Unique
    HashSet<String> youshallnotgrief$blacklistedModules = new HashSet<>() {{
        add("minecraft");
        add("fabricmc");
        add("forge");
        add("neoforge");
        add("google");
        add("unimi");
        add("mojang");
        add(YouShallNotGriefMod.MOD_ID);
    }};

    @Unique
    HashMap<String, String> youshallnotgrief$stackPathToModID = new HashMap<>();


    //TODO: Redo this to be more intelligent
    @Inject(at = @At("RETURN"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private void youshallnotgrief$logModdedSetBlockInteractions(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        //Only log if the block was actually set
        if (!cir.getReturnValue()){
            return;
        }

        Level level = (Level) (Object) this;
        if(level.isClientSide){
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int causeTraceIndex;
        for (causeTraceIndex = 2; causeTraceIndex < stackTraceElements.length - 1; causeTraceIndex++) {
            if(youshallnotgrief$containsAny(stackTraceElements[causeTraceIndex].toString())){
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

        String moduleName = "@" + youshallnotgrief$getModIDFromClassName(className);
        if(moduleName.contains("java")){
            return;
        }

        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        className = className.substring(className.lastIndexOf(".") + 1);
        String fullName = className + ":" + methodName;

        BlockUtils.addToDatabase(blockPos, level, level.getBlockState(blockPos), blockState, moduleName, fullName);
    }

    @Unique
    private boolean youshallnotgrief$containsAny(String stackTraceModule) {
        for (String blacklistedModule : youshallnotgrief$blacklistedModules) {
            if (stackTraceModule.contains(blacklistedModule)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private String youshallnotgrief$getModIDFromClassName(String className){
        String[] elementParts = className.split("\\.");
        String moduleName = elementParts[0] + "." + elementParts[1] + "." + elementParts[2];

        if(youshallnotgrief$stackPathToModID.containsKey(moduleName)){
            return youshallnotgrief$stackPathToModID.get(moduleName);
        }

        Platform.getModIds().forEach(modID -> {
            if(moduleName.contains(modID)){
                youshallnotgrief$stackPathToModID.put(moduleName, modID);
            }
        });

        if(youshallnotgrief$stackPathToModID.containsKey(moduleName)){
            return youshallnotgrief$stackPathToModID.get(moduleName);
        }

        return moduleName;
    }
}
