package net.youshallnotgrief.mixin.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.block.BlockSetAction;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;

@Mixin(value = Level.class)
public abstract class BlockMixin {

    @Unique
    HashSet<String> blacklistedModules = new HashSet<>() {{
        add("minecraft");
        add("fabricmc");
    }};

    @Unique
    HashMap<String, String> stackPathToModID = new HashMap<>();

    @Inject(at = @At("TAIL"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    private void youshallnotgrief$detectModdedSetBlockInteractions(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<Boolean> cir) {
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

        String moduleName = "#" + youshallnotgrief$getModIDFromClassName(className);
        if(moduleName.contains("java")){
            return;
        }

        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        className = className.substring(className.lastIndexOf(".") + 1);
        String fullName = className + ":" + methodName;

        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetDataFromModdedInteraction(blockPos, level, moduleName, fullName));
    }

    @Unique
    private boolean youshallnotgrief$containsAny(String stackTraceModule) {
        for (String blacklistedModule : blacklistedModules) {
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
