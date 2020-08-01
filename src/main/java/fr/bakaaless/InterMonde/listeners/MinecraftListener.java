package fr.bakaaless.InterMonde.listeners;

import fr.bakaaless.InterMonde.commands.Synchronisation;
import fr.bakaaless.InterMonde.config.Config;
import fr.bakaaless.InterMonde.plugin.InterMonde;
import net.dv8tion.jda.api.OnlineStatus;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class MinecraftListener implements Listener {

    public MinecraftListener() {
        MinecraftListener.sendDiscord(Config.getFormatDiscordStart());
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent e) {
        if (InterMonde.isHide(e.getPlayer().getUniqueId()))
            return;
        sendDiscord(e.getPlayer().getUniqueId(), Config.getFormatDiscordChat().replace("{1}", e.getMessage()));
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        if (InterMonde.isHide(e.getPlayer().getUniqueId()))
            return;
        InterMonde.getInstance().getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        sendDiscord(e.getPlayer().getUniqueId(), Config.getFormatDiscordJoin());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        Synchronisation.getSynchronisation().remove(e.getPlayer().getUniqueId());
        Synchronisation.getUser().remove(e.getPlayer().getUniqueId());
        if (InterMonde.isHide(e.getPlayer().getUniqueId()))
            return;
        if (InterMonde.getInstance().getServer().getOnlinePlayers().size() == 1)
            InterMonde.getInstance().getJda().getPresence().setStatus(OnlineStatus.IDLE);
        sendDiscord(e.getPlayer().getUniqueId(), Config.getFormatDiscordQuit());
    }

    private void sendDiscord(final UUID uuid, final String message) {
        new Thread(() -> {
            final String id = InterMonde.getDiscordIdFromUUID(uuid);
            String name = "";
            if (!id.equals(""))
                if (InterMonde.getInstance().getJda().getGuildById(Config.getServerId()) == null || InterMonde.getInstance().getJda().getGuildById(Config.getServerId()).getMemberById(id) == null)
                    name = "";
                else {
                    if (InterMonde.getInstance().getJda().getGuildById(Config.getServerId()).getMemberById(id).getEffectiveName().equals(Bukkit.getOfflinePlayer(uuid).getName()))
                        name = Bukkit.getOfflinePlayer(uuid).getName();
                    else
                        name = InterMonde.getInstance().getJda().getGuildById(Config.getServerId()).getMemberById(id).getEffectiveName() + " (" + Bukkit.getOfflinePlayer(uuid).getName() + ")";
                }
            if (name == null | name.equals(""))
                name = InterMonde.getInstance().getServer().getOfflinePlayer(uuid).getName();
            sendDiscord(message.replace("{0}", name));
        }).start();
    }

    public static void sendDiscord(final String message) {
        if (message.equals("")) return;
        InterMonde.getInstance().getJda().getTextChannelById(Config.getChannelId()).sendMessage(message).complete();
    }
}
