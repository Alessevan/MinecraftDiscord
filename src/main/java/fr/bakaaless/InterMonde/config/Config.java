package fr.bakaaless.InterMonde.config;

import fr.bakaaless.InterMonde.plugin.InterMonde;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public class Config {

    private static final String path = InterMonde.getInstance().getDataFolder().getAbsolutePath();

    private static String token = "";
    private static String serverId = "";
    private static String channelId = "";
    private static String formatDiscordChat = "{0} » {1}";
    private static String formatDiscordJoin = "{0} a rejoint le serveur.";
    private static String formatDiscordQuit = "{0} a quitté le serveur.";
    private static String formatDiscordStart = "Serveur ouvert.";
    private static String formatDiscordStop = "Serveur fermé.";
    private static String formatMinecraft = "&3{0} &8» &7{1}";

    public static void init() {
        final File file = new File(path, "config.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                yamlConfiguration.set("token", token);
                yamlConfiguration.set("serverId", serverId);
                yamlConfiguration.set("channelId", channelId);
                yamlConfiguration.set("format.discord.chat", formatDiscordChat);
                yamlConfiguration.set("format.discord.join", formatDiscordJoin);
                yamlConfiguration.set("format.discord.quit", formatDiscordQuit);
                yamlConfiguration.set("format.discord.start", formatDiscordStart);
                yamlConfiguration.set("format.discord.stop", formatDiscordStop);
                yamlConfiguration.set("format.minecraft", formatMinecraft);
                yamlConfiguration.save(file);
            }
            catch (Exception e) {
                InterMonde.getInstance().getLogger().log(Level.SEVERE, "Can't write file configuration.", e);
            }
        }
        else {
            try {
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                token = yamlConfiguration.getString("token");
                serverId = yamlConfiguration.getString("serverId");
                channelId = yamlConfiguration.getString("channelId");
                formatDiscordChat = yamlConfiguration.getString("format.discord.chat");
                formatDiscordJoin = yamlConfiguration.getString("format.discord.join");
                formatDiscordQuit = yamlConfiguration.getString("format.discord.quit");
                formatDiscordStart = yamlConfiguration.getString("format.discord.start");
                formatDiscordStop = yamlConfiguration.getString("format.discord.stop");
                formatMinecraft = yamlConfiguration.getString("format.minecraft");
            }
            catch (Exception e) {
                InterMonde.getInstance().getLogger().log(Level.SEVERE, "Can't load file configuration.", e);
            }
        }
    }

    public static String getPath() {
        return path;
    }

    public static String getToken() {
        return token;
    }

    public static String getServerId() {
        return serverId;
    }

    public static String getChannelId() {
        return channelId;
    }

    public static String getFormatDiscordChat() {
        return formatDiscordChat;
    }

    public static String getFormatDiscordJoin() {
        return formatDiscordJoin;
    }

    public static String getFormatDiscordQuit() {
        return formatDiscordQuit;
    }

    public static String getFormatDiscordStart() {
        return formatDiscordStart;
    }

    public static String getFormatDiscordStop() {
        return formatDiscordStop;
    }

    public static String getFormatMinecraft() {
        return formatMinecraft;
    }
}
