package fr.bakaaless.InterMonde.api;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICommand {

    boolean handle(final @NotNull CommandSender commandSender, final @NotNull List<String> args);
}
