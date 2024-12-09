package net.youshallnotgrief.mixin;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.MixinDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;

@Mixin(value = Level.class, priority = 10100)
public class LevelMixin {

    @Unique
    HashSet<String> youshallnotgrief$vanillaModules = new HashSet<>() {{
        add("minecraft");
        add("java.");
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

    @Unique
    HashSet<String> youshallnotgrief$debugLoggedInteractions = new HashSet<>();

    //This variable is used to determine how deep in the callstack we are
    //this is used when level.setBlock is called recursively for neighbour updates
    //so that we know whether to log them or not
    @Unique
    int youshallnotgrief$callDepth = 0;

    @Unique
    BlockState youshallnotgrief$oldBlockState = null;

    @Inject(at = @At("HEAD"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private void youshallnotgrief$captureOldBlock(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if(level.isClientSide){
            return;
        }

        youshallnotgrief$callDepth++;
        if(youshallnotgrief$callDepth == 1) {
            youshallnotgrief$oldBlockState = level.getBlockState(blockPos);
        }
    }

    @Inject(at = @At("RETURN"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private void youshallnotgrief$logSetBlockInteractions(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if(level.isClientSide){
            return;
        }

        //TODO: Fix if this becomes an issue
        //Only level sets with a depth of 1 will be logged, this is because wasLevelSetTracked is true in this case
        //this means that some things which rely on chained block updates may not be logged
        youshallnotgrief$handleLoggingBlock(blockPos, blockState, cir);

        if(youshallnotgrief$callDepth == 1) {
            MixinDataHolder.wasLevelSetTracked = false;
            youshallnotgrief$oldBlockState = null;
        }

        youshallnotgrief$callDepth--;
    }

    @Unique
    private void youshallnotgrief$handleLoggingBlock(BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<Boolean> cir){
        //Only log if the block was actually set
        if (!cir.getReturnValue()){
            return;
        }

        //If we are not logging fallback or modded blocks, we can skip this entire method
        if(!ServerConfig.logFallbackLevelSets.get() && !ServerConfig.logModdedLevelSets.get()){
            return;
        }

        if(youshallnotgrief$oldBlockState == null){
            youshallnotgrief$oldBlockState = Blocks.AIR.defaultBlockState();
        }

        if(youshallnotgrief$oldBlockState.getBlock() == blockState.getBlock()){
            return;
        }

        String skippedBlockLog = MessageFormat.format("Skipping block logging from {0} to {1} due to chained block update call depth", youshallnotgrief$oldBlockState.getBlock(), blockState.getBlock());
        if(youshallnotgrief$shouldLogDebugInfo(skippedBlockLog) && youshallnotgrief$callDepth > 1){
            YouShallNotGriefMod.LOGGER.warn("{} {} at {}", skippedBlockLog, youshallnotgrief$callDepth, blockPos);
        }

        Level level = (Level) (Object) this;
        if(MixinDataHolder.wasLevelSetTracked){
            return;
        }

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        //This loop exists to get the cause of the levelSet by analysing the stackTrace
        int causeTraceIndex = 0;
        for (int i = 3; i < stackTraceElements.length - 1; i++) {

            //We should stop at the first class which is not a vanilla module
            if(!youshallnotgrief$isVanillaFunction(stackTraceElements[i].toString())){
                causeTraceIndex = i;
                break;
            }
        }

        //This means that all the functions in the stacktrace were vanilla as causeTraceIndex was never set
        boolean isVanillaInteraction = causeTraceIndex == 0;
        if(isVanillaInteraction){
            StringBuilder uncategorizedBlockLog = new StringBuilder(MessageFormat.format("Uncategorized level set occurred from {0} to {1}:", youshallnotgrief$oldBlockState, blockState));
            for (int i = 3; i < stackTraceElements.length - 1; i++) {
                uncategorizedBlockLog.append("\n  ").append(stackTraceElements[i]);
            }

            if(youshallnotgrief$shouldLogDebugInfo(uncategorizedBlockLog.toString())) {
                YouShallNotGriefMod.LOGGER.warn(uncategorizedBlockLog.toString());
            }

            for (int i = 3; i < stackTraceElements.length - 1; i++) {
                //Find the first line which is not part of the level package
                if(!stackTraceElements[i].toString().contains(".Level.")){
                    causeTraceIndex = i;
                    break;
                }
            }
        }

        //Get the cause, modid, function name etc. from the stack trace elements
        StackTraceElement causeElement = stackTraceElements[causeTraceIndex];
        String methodName = causeElement.getMethodName();
        String className = causeElement.getClassName();

        String moduleName;
        if(isVanillaInteraction) {
            if(!ServerConfig.logFallbackLevelSets.get()){
                return;
            }
            moduleName = "@minecraft";

        }else{
            if(!ServerConfig.logModdedLevelSets.get()){
                return;
            }
            moduleName = "@" + youshallnotgrief$getModIDFromClassName(className);
        }

        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        className = className.substring(className.lastIndexOf(".") + 1);
        String fullName = className + ":" + methodName;

        BlockUtils.addToDatabase(blockPos, level, youshallnotgrief$oldBlockState, blockState, moduleName, fullName);
    }

    @Unique
    private boolean youshallnotgrief$shouldLogDebugInfo(String log){
        if(Platform.isDevelopmentEnvironment() || ServerConfig.debugLogUnhandledBlockSets.get()){
            if(ServerConfig.debugLogMultipleTimes.get()){
                return true;
            }

            if(youshallnotgrief$debugLoggedInteractions.contains(log)){
                return false;
            }

            youshallnotgrief$debugLoggedInteractions.add(log);
            return true;
        }
        return false;
    }

    @Unique
    private boolean youshallnotgrief$isVanillaFunction(String stackTraceLine){
        for (String module : youshallnotgrief$vanillaModules) {
            if (stackTraceLine.contains(module)) {
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
