package fr.bakaaless.InterMonde.commands;

import fr.bakaaless.InterMonde.api.CommandAnnotation;
import fr.bakaaless.InterMonde.api.ICommand;
import fr.bakaaless.InterMonde.lang.Lang;
import fr.bakaaless.InterMonde.mysql.DBConnection;
import fr.bakaaless.InterMonde.permissions.Permissions;
import fr.bakaaless.InterMonde.plugin.InterMonde;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Import implements ICommand {

    @CommandAnnotation(
            command = { "import" },
            description = "Commande pour importer un fichier yml vers la base de données.",
            usage = "intermonde import <path>",
            permission = Permissions.IMPORT
    )
    @Override
    public boolean handle(@NotNull CommandSender commandSender, @NotNull List<String> args) {
        final String[] filePart = args.get(0).split("/");
        final StringBuilder additionalPath = new StringBuilder();
        for (int i = 0; i < filePart.length - 1; i++)
            additionalPath.append("/").append(filePart[i]);
        final File path = new File(InterMonde.getInstance().getDataFolder().getAbsolutePath(), additionalPath.toString());
        if (!path.exists()) {
            path.mkdirs();
        }
        final File file = new File(path, filePart[filePart.length - 1]);
        if (!file.exists()) {
            commandSender.sendMessage(Lang.ERROR_FILE.get());
            return false;
        }
        try {
            final YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(file);
            int success = 0;
            int newers = 0;
            int updated = 0;
            int errors = 0;
            for (final String uuid : yamlConfiguration.getConfigurationSection("players").getKeys(false)) {
                try {
                    final String discordId = yamlConfiguration.getString("players." + uuid + ".discordId");
                    final String hide = yamlConfiguration.getString("players." + uuid + ".hide");
                    if (InterMonde.getDiscordIdFromUUID(uuid).equals("")) {
                        DBConnection.sql.modifyQuery("INSERT INTO `players` (`uuid`, `discordId`, `hide`) VALUES ('" + uuid + "', '" + discordId + "', '" + hide + "')");
                    } else {
                        DBConnection.sql.modifyQuery("UPDATE `players` SET `discordId` = '" + discordId + "', `hide` = '" + hide + "' WHERE `uuid` = '" + uuid + "'");
                    }
                    success++;
                }
                catch (Exception e) {
                    errors++;
                }
            }
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m<===================>"));
            commandSender.sendMessage("");
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lSuccès : &a&l" + success));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&lNouveau(x) : &3&l" + newers));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&lUpdate(s) : &7&l" + updated));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lErreur(s) : &6&l" + errors));
            commandSender.sendMessage("");
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m<===================>"));
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return true;
    }

}
