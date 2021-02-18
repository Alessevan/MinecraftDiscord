package fr.bakaaless.InterMonde.lang;

import fr.bakaaless.InterMonde.plugin.InterMonde;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum Lang {

    PREFIX_INFO("prefix.info", "&3InterMonde &8&l»&r"),
    PREFIX_ERROR("prefix.error", "&cInterMonde &4&l»&r"),

    INFO_DISCORD_SYNC_CODE("info.discord.sync.code", "Afin de vérifier que vous êtes bien {0}, vous devez entrez dans le chat minecraft `/intermonde sync {1}`."),
    INFO_MINECRAFT_SYNC("info.minecraft.sync", "%prefix_info% &7Séquence de synchronisation commencée. Vous venez de recevoir un code en message privé pour lier votre compte."),
    INFO_LINK("info.link", "%prefix_info% &7Vous êtes maintenant relié à discord."),
    INFO_HIDE("info.hide", "%prefix_info% &7Vous êtes maintenant caché."),
    INFO_SHOW("info.show", "%prefix_info% &7Vous êtes maintenant visible."),

    ERROR_PERMISSION("error.permission", "%prefix_error% &cVous ne pouvez pas faire cela."),
    ERROR_USAGE("error.usage", "%prefix_error% &cUtilisation : &4{0}&c."),
    ERROR_UNKNOWN("error.unknown", "%prefix_error% &cCommand inconnue."),
    ERROR_CONFIG("error.config", "%prefix_error% &cPlugin non configuré."),
    ERROR_FILE("error.file", "%prefix_error% &cFichier introuvable"),
    ERROR_LINKED("error.linked", "%prefix_error% &cVous n'êtes pas relié à un compte discord."),
    ERROR_CONSOLE("error.console", "%prefix_error% &cSeul un joueur peut exécuter cette commande."),
    ERROR_LINK("error.link.code", "%prefix_error% &cVous vous êtes trompé de code. Séquence de synchronisation annulée."),
    ERROR_MESSAGE("error.link.message", "%prefix_error% &cImpossible d'envoyer un message privé à ce compte discord."),
    ERROR_MINECRAFT_LINK("error.link.minecraft", "%prefix_error% &cVotre compte minecraft est déjà lié."),
    ERROR_DISCORD_LINK("error.link.discord", "%prefix_error% &cCe compte discord est déjà lié.");

    private final String path;
    private String message;

    Lang(final String path, final String message) {
        this.path = path;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String get() {
        return this.get("");
    }

    public String get(final String... replacement) {
        String message = this.message
                .replace("%prefix_info%", PREFIX_INFO.message)
                .replace("%prefix_error%", PREFIX_ERROR.message);
        for (int i = 0; i < replacement.length; i++) {
            if (replacement[i].equals("")) continue;
            message = message.replace("{" + i + "}", replacement[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public String toString() {
        return message;
    }

    public static void init() {
        final File file = new File(InterMonde.getInstance().getDataFolder(), "lang.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                for (final Lang lang : values()) {
                    yamlConfiguration.set(lang.path, lang.message);
                }
                yamlConfiguration.save(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                for (final Lang lang : values()) {
                    if (yamlConfiguration.get(lang.path) == null)
                        yamlConfiguration.set(lang.path, lang.message);
                    else
                        lang.setMessage(yamlConfiguration.getString(lang.path));
                }
                yamlConfiguration.save(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
}
