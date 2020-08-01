package fr.bakaaless.InterMonde.commands;

import fr.bakaaless.InterMonde.api.CommandAnnotation;
import fr.bakaaless.InterMonde.api.ICommand;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Export implements ICommand {

    @CommandAnnotation(
            command = { "export" },
            description = "Commande pour exporter la base de données en fichier yml.",
            usage = "intermonde export <path>",
            permission = Permissions.EXPORT
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
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new Thread(() -> {
            final YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(file);
                int success = 0;
                int errors = 0;
                try (final ResultSet resultSet = DBConnection.sql.readQuery("SELECT * FROM `players`")) {
                    while (resultSet.next()) {
                        try {
                            final String uuid = resultSet.getString("uuid");
                            yamlConfiguration.set("players." + uuid + ".discordId", resultSet.getString("discordId"));
                            yamlConfiguration.set("players." + uuid + ".hide", resultSet.getString("hide"));
                            success++;
                        }
                        catch (Exception ignored) {
                            errors++;
                        }
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                yamlConfiguration.save(file);
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m<===================>"));
                commandSender.sendMessage("");
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lSuccès : &a&l" + success));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lErreur(s) : &6&l" + errors));
                commandSender.sendMessage("");
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m<===================>"));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

        }).start();
        return true;
    }

}
