package net.youshallnotgrief.mixin;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.YouShallNotGriefMod;
import net.youshallnotgrief.util.BlockUtils;
import net.youshallnotgrief.util.MixinDataHolder;
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
    HashSet<String> youshallnotgrief$vanillaModules = new HashSet<>() {{
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

    @Unique
    BlockState youshallnotgrief$oldBlockState = null;

    @Inject(at = @At("HEAD"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private void youshallnotgrief$captureOldBlock(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if(level.isClientSide){
            return;
        }
        youshallnotgrief$oldBlockState = level.getBlockState(blockPos);
    }

    @Inject(at = @At("RETURN"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private void youshallnotgrief$logSetBlockInteractions(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if(level.isClientSide){
            return;
        }

        youshallnotgrief$handleLoggingBlock(blockPos, blockState, cir);
        MixinDataHolder.wasLevelSetTracked = false;
        youshallnotgrief$oldBlockState = null;
    }

    @Unique
    private void youshallnotgrief$handleLoggingBlock(BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<Boolean> cir){
        //Only log if the block was actually set
        if (!cir.getReturnValue()){
            return;
        }

        Level level = (Level) (Object) this;
        if(MixinDataHolder.wasLevelSetTracked){
            return;
        }

        if(youshallnotgrief$oldBlockState == null){
            youshallnotgrief$oldBlockState = Blocks.AIR.defaultBlockState();
        }

        if(youshallnotgrief$oldBlockState.getBlock() == blockState.getBlock()){
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

        //This means that we all the functions in the stacktrace were vanilla as causeTraceIndex was never set
        if(causeTraceIndex == 0){

            //TODO: Make a config which can override this for non dev environments
            if(Platform.isDevelopmentEnvironment()) {
                YouShallNotGriefMod.LOGGER.warn("Uncategorized level set occurred from {} to {}:", youshallnotgrief$oldBlockState, blockState);
                for (int i = 3; i < stackTraceElements.length - 1; i++) {
                    YouShallNotGriefMod.LOGGER.warn("  {}", stackTraceElements[i]);
                }
            }

            for (int i = 3; i < stackTraceElements.length - 1; i++) {
                //Find the first line which is not part of the level package
                if(!stackTraceElements[i].toString().contains(".Level.")){
                    causeTraceIndex = i;
                    break;
                }
            }
        }

        //Get the cause, modid, function name etc from the stack trace elements
        StackTraceElement causeElement = stackTraceElements[causeTraceIndex];
        String methodName = causeElement.getMethodName();
        String className = causeElement.getClassName();

        String moduleName = "@" + youshallnotgrief$getModIDFromClassName(className);

        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        className = className.substring(className.lastIndexOf(".") + 1);
        String fullName = className + ":" + methodName;

        BlockUtils.addToDatabase(blockPos, level, youshallnotgrief$oldBlockState, blockState, moduleName, fullName);
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
