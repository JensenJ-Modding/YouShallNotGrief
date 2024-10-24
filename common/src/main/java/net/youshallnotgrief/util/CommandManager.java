package net.youshallnotgrief.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
public class CommandManager {

    public static void registerCommands() {
        CommandRegistrationEvent.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            commandDispatcher.register(Commands.literal("inspect")
                    .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                    .executes(CommandManager::toggleInspectMode));

            commandDispatcher.register(Commands.literal("i")
                    .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                    .executes(CommandManager::toggleInspectMode));
        });

        CommandRegistrationEvent.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            commandDispatcher.register(Commands.literal("page")
                    .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                            .executes(context -> showPage(context, IntegerArgumentType.getInteger(context, "page")))
                    )
            );

            commandDispatcher.register(Commands.literal("p")
                    .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                            .executes(context -> showPage(context, IntegerArgumentType.getInteger(context, "page")))
                    )
            );
        });
    }

    private static int toggleInspectMode(CommandContext<CommandSourceStack> context){
        CommandSourceStack stack = context.getSource();

        if(!stack.isPlayer()){
            stack.sendFailure(Component.literal("The console cannot enter inspect mode."));
            return 0;
        }

        ServerPlayer player = stack.getPlayer();
        InspectionMode.toggleInspectMode(player);
        return 1;
    }

    private static int showPage(CommandContext<CommandSourceStack> context, int pageNumber){
        CommandSourceStack stack = context.getSource();

        if(!stack.isPlayer()){
            stack.sendFailure(Component.literal("The console cannot show pages."));
            return 0;
        }

        ServerPlayer player = stack.getPlayer();
        InspectionMode.showDetails(player, pageNumber - 1);
        return 1;
    }
}
