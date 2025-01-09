package net.youshallnotgrief.inspection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.youshallnotgrief.config.ServerConfig;
import net.youshallnotgrief.util.MiscUtils;

import java.util.Collection;

public class CommandManager {
    public static void registerCommands() {
        CommandRegistrationEvent.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            registerInspectCommand(new String[]{"inspect", "i"}, commandDispatcher);
            registerPageCommand(new String[]{"page", "p"}, commandDispatcher);
        });
    }

    private static void registerInspectCommand(String[] aliases, CommandDispatcher<CommandSourceStack> dispatcher){
        for(String alias : aliases){
            dispatcher.register(Commands.literal(alias)
                    .requires((commandSourceStack) -> commandSourceStack.hasPermission(ServerConfig.inspectionOpNeeded.get() ? 1 : 0))
                    .executes(CommandManager::toggleInspectMode).then(Commands.argument("targets", EntityArgument.players())
                            .executes(CommandManager::toggleInspectModeOther)
                    )
            );
        }
    }

    private static void registerPageCommand(String[] aliases, CommandDispatcher<CommandSourceStack> dispatcher){
        for(String alias : aliases){
            dispatcher.register(Commands.literal(alias).then(Commands.argument("page", IntegerArgumentType.integer(1))
                    .executes(context -> showPage(context, IntegerArgumentType.getInteger(context, "page"))))
            );
        }
    }

    private static int toggleInspectMode(CommandContext<CommandSourceStack> context) {
        CommandSourceStack stack = context.getSource();
        if(!stack.isPlayer()){
            stack.sendFailure(Component.literal("The console cannot enter inspect mode."));
            return 0;
        }

        ServerPlayer player = stack.getPlayer();
        InspectionMode.toggleInspectMode(player);
        return 1;
    }

    private static int toggleInspectModeOther(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack stack = context.getSource();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

        for(ServerPlayer player : players){
            if(stack.getPlayer() != player){
                if(InspectionMode.isPlayerInspecting(player)){
                    stack.sendSuccess(() -> Component.translatable("msg.youshallnotgrief.inspection.disable.other", player.getName()).withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))), true);
                }else{
                    stack.sendSuccess(() -> Component.translatable("msg.youshallnotgrief.inspection.enable.other", player.getName()).withStyle(style -> style
                            .withColor(MiscUtils.getTextColourFromConfig(ServerConfig.inspectionPrimaryColour.get()))), true);
                }
            }
            InspectionMode.toggleInspectMode(player);
        }

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
