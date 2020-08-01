package fr.bakaaless.InterMonde.listeners;

import fr.bakaaless.InterMonde.config.Config;
import fr.bakaaless.InterMonde.plugin.InterMonde;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull final MessageReceivedEvent e) {
        if (!e.getChannelType().equals(ChannelType.TEXT))
            return;
        if (!e.getGuild().getId().equalsIgnoreCase(Config.getServerId()) || !e.getChannel().getId().equalsIgnoreCase(Config.getChannelId())) return;
        if (Config.getFormatMinecraft().equals("")) return;
        if (e.getAuthor().isBot()) return;
        if (e.getMember() == null) return;
        if (e.getMember().getUser().equals(InterMonde.getInstance().getJda().getSelfUser())) return;
        final String uuid = InterMonde.getUUIDFromDiscordId(e.getMember());
        String name = "";
        if (!uuid.equalsIgnoreCase(""))
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        if (name == null || name.equals(""))
            name = e.getMember().getEffectiveName();
        sendBukkitMessage(Config.getFormatMinecraft().replace("{0}", name).replace("{1}", e.getMessage().getContentDisplay()));
    }

    private void sendBukkitMessage(final String message) {
        InterMonde.getInstance().getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
