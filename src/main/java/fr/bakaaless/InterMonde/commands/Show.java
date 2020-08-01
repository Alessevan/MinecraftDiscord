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

public class Show implements ICommand {

    @CommandAnnotation(
            command = { "show", "visible" },
            description = "Commande pour être visible dans l'intermonde.",
            usage = "intermonde show",
            permission = Permissions.SHOW
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
        DBConnection.sql.modifyQuery("UPDATE `players` SET `hide` = 'false' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
        player.sendMessage(Lang.INFO_SHOW.get());
        return true;
    }

}
