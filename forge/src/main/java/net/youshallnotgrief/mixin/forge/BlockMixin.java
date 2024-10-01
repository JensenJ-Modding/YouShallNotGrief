package net.youshallnotgrief.mixin.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.youshallnotgrief.data.BlockSetActions;
import net.youshallnotgrief.database.DatabaseManager;
import net.youshallnotgrief.util.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;

@Mixin(value = Level.class)
public abstract class BlockMixin {

    @Unique
    HashSet<String> youshallnotsteal$blacklistedModules = new HashSet<>() {{
        add("minecraft");
        add("forge");
    }};

    @Inject(at = @At("TAIL"), method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    private void youshallnotgrief$detectModdedSetBlockInteractions(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<Boolean> cir) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int causeTraceIndex;
        for (causeTraceIndex = 2; causeTraceIndex < stackTraceElements.length - 1; causeTraceIndex++) {
            if(Objects.equals(stackTraceElements[causeTraceIndex].getModuleName(), "java.base")){
                return;
            }

            if(youshallnotgrief$containsAny(stackTraceElements[causeTraceIndex].getModuleName())){
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
        String moduleName = "#" + causeElement.getModuleName();
        String methodName = causeElement.getMethodName();
        String className = causeElement.getClassName();
        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        className = className.substring(className.lastIndexOf(".") + 1);
        String causeDesc = className + ":" + methodName;
        Level level = (Level) (Object) this;
        DatabaseManager.BLOCK_SET_MANAGER.addToDatabase(BlockUtils.makeBlockSetData(blockPos, level, BlockSetActions.SET, moduleName, causeDesc));
    }

    @Unique
    private boolean youshallnotgrief$containsAny(String stackTraceModule) {
        for (String blacklistedModule : youshallnotsteal$blacklistedModules) {
            if (stackTraceModule.contains(blacklistedModule)) {
                return true;
            }
        }
        return false;
    }

}
