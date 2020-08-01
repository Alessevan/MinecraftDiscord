package fr.bakaaless.InterMonde.plugin;

import fr.bakaaless.InterMonde.commands.CommandManager;
import fr.bakaaless.InterMonde.config.Config;
import fr.bakaaless.InterMonde.lang.Lang;
import fr.bakaaless.InterMonde.listeners.DiscordListener;
import fr.bakaaless.InterMonde.listeners.MinecraftListener;
import fr.bakaaless.InterMonde.mysql.DBConnection;
import fr.bakaaless.InterMonde.permissions.Permissions;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class InterMonde extends JavaPlugin {

    @Getter
    private static InterMonde instance;

    @Getter
    private JDA jda;

    public static String getUUIDFromDiscordId(final Member member) {
        return getUUIDFromDiscordId(member.getId());
    }

    public static String getUUIDFromDiscordId(final String id) {
        try (final ResultSet resultSet = DBConnection.sql.readQuery("SELECT * FROM `players` WHERE `discordId` = '" + id + "'")) {
            if (resultSet.next())
                return resultSet.getString("uuid");
        }
        catch (SQLException ignored) {
            return "";
        }
        return "";
    }

    public static String getDiscordIdFromUUID(final UUID uuid) {
        return getDiscordIdFromUUID(uuid.toString());
    }

    public static String getDiscordIdFromUUID(final String uuid) {
        try (final ResultSet resultSet = DBConnection.sql.readQuery("SELECT * FROM `players` WHERE `uuid` = '" + uuid + "'")) {
            if (resultSet.next())
                return resultSet.getString("discordId");
        }
        catch (SQLException ignored) {
            return "";
        }
        return "";
    }

    public static boolean isHide(final UUID uuid) {
        return isHide(uuid.toString());
    }

    public static boolean isHide(final String uuid) {
        try (final ResultSet resultSet = DBConnection.sql.readQuery("SELECT * FROM `players` WHERE `uuid` = '" + uuid + "'")) {
            if (resultSet.next()) {
                return resultSet.getString("hide").equals("true");
            }
        }
        catch (SQLException ignored) {
            return false;
        }
        return false;
    }

    @Override
    public void onEnable() {
        InterMonde.instance = this;
        DBConnection.init(this);
        Config.init();
        Lang.init();
        Permissions.init();
        new Thread(() -> {
            try {
                this.jda = JDABuilder.createDefault(
                            Config.getToken(),
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES
                        )
                        .disableCache(
                                CacheFlag.VOICE_STATE,
                                CacheFlag.EMOTE
                        )
                        .setStatus(OnlineStatus.IDLE)
                        .setChunkingFilter(ChunkingFilter.NONE)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .addEventListeners(new DiscordListener())
                        .setActivity(Activity.watching("les messages"))
                        .build();
                this.jda.awaitReady();
                this.getServer().getPluginManager().registerEvents(new MinecraftListener(), this);
                new CommandManager();
                if (this.getServer().getOnlinePlayers().size() > 0)
                    this.jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
                onDisable();
            }
        }).start();
    }

    @Override
    public void onDisable() {
        if (this.jda != null) {
            MinecraftListener.sendDiscord(Config.getFormatDiscordStop());
            this.jda.shutdownNow();
        }
    }
}
