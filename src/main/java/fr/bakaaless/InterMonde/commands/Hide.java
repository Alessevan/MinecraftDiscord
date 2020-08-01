package fr.bakaaless.InterMonde.commands;

import fr.bakaaless.InterMonde.api.CommandAnnotation;
import fr.bakaaless.InterMonde.api.ICommand;
import fr.bakaaless.InterMonde.lang.Lang;
import fr.bakaaless.InterMonde.mysql.DBConnection;
import fr.bakaaless.InterMonde.permissions.Permissions;
import fr.bakaaless.InterMonde.plugin.InterMonde;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Hide implements ICommand {

    @CommandAnnotation(
            command = { "hide", "invisible", "vanish" },
            description = "Commande pour être invisible dans l'intermonde.",
            usage = "intermonde hide",
            permission = Permissions.HIDE
    )
    @Override
    public boolean handle(@NotNull CommandSender commandSender, @NotNull List<String> args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Lang.ERROR_CONSOLE.get());
            return false;
        }
        final Player player = (Player) commandSender;
        if (InterMonde.getDiscordIdFromUUID(player.getUniqueId()).equals("")) {
            player.sendMessage(Lang.ERROR_LINKED.get());
            return false;
        }
        DBConnection.sql.modifyQuery("UPDATE `players` SET `hide` = 'true' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
        player.sendMessage(Lang.INFO_HIDE.get());
        return true;
    }

}
