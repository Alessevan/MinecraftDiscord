package fr.bakaaless.InterMonde.commands;

import fr.bakaaless.InterMonde.api.CommandAnnotation;
import fr.bakaaless.InterMonde.api.ICommand;
import fr.bakaaless.InterMonde.permissions.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public class Help implements ICommand {

    @CommandAnnotation(
            command = { "help" },
            description = "Commande pour afficher l'aide.",
            permission = Permissions.HELP
    )
    @Override
    public boolean handle(@NotNull CommandSender commandSender, @NotNull List<String> args) {
        for (final ICommand commandCore : CommandManager.getCommands()) {
            for (final Method method : commandCore.getClass().getDeclaredMethods()) {
                final CommandAnnotation annotationCommand = method.getDeclaredAnnotation(CommandAnnotation.class);
                if (annotationCommand == null) continue;
                commandSender.sendMessage("§3" + annotationCommand.usage() + " §9<:> §b" + annotationCommand.description());
            }
        }
        return true;
    }
}
