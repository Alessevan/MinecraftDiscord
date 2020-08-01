package fr.bakaaless.InterMonde.commands;

import fr.bakaaless.InterMonde.api.CommandAnnotation;
import fr.bakaaless.InterMonde.api.ICommand;
import fr.bakaaless.InterMonde.lang.Lang;
import fr.bakaaless.InterMonde.plugin.InterMonde;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager implements CommandExecutor {

    @Getter()
    private static List<ICommand> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new Export());
        commands.add(new Hide());
        commands.add(new Import());
        commands.add(new Show());
        commands.add(new Synchronisation());
        for (final String command : InterMonde.getInstance().getDescription().getCommands().keySet()) {
            try {
                assert InterMonde.getInstance().getCommand(command) != null;
                Objects.requireNonNull(InterMonde.getInstance().getCommand(command)).setExecutor(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Lang.ERROR_UNKNOWN.get());
            return false;
        }
        for (final ICommand commandCore : commands) {
            for (final Method method : commandCore.getClass().getDeclaredMethods()) {
                final CommandAnnotation annotationCommand = method.getDeclaredAnnotation(CommandAnnotation.class);
                if (annotationCommand == null) continue;
                for (final String cmds : annotationCommand.command()) {
                    if (cmds.equalsIgnoreCase(args[0])) {
                        if (annotationCommand.permission().hasPermission(sender)) {
                            List<String> arguments = new ArrayList<>();
                            int i = -1;
                            for (final String arg : args) {
                                i++;
                                if (i == 0) continue;
                                arguments.add(arg);
                            }
                            try {
                                return (boolean) method.invoke(commandCore, sender, arguments);
                            } catch (Exception e) {
                                sender.sendMessage(Lang.ERROR_USAGE.get(annotationCommand.usage()));
                                e.printStackTrace();
                                return false;
                            }
                        } else {
                            sender.sendMessage(Lang.ERROR_PERMISSION.get());
                            return false;
                        }
                    }
                }
            }
        }
        sender.sendMessage(Lang.ERROR_UNKNOWN.get());
        return false;
    }

}
