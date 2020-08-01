package fr.bakaaless.InterMonde.commands;

import fr.bakaaless.InterMonde.api.CommandAnnotation;
import fr.bakaaless.InterMonde.api.ICommand;
import fr.bakaaless.InterMonde.config.Config;
import fr.bakaaless.InterMonde.lang.Lang;
import fr.bakaaless.InterMonde.mysql.DBConnection;
import fr.bakaaless.InterMonde.permissions.Permissions;
import fr.bakaaless.InterMonde.plugin.InterMonde;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Synchronisation implements ICommand {

    @Getter
    private static Map<UUID, String> synchronisation;
    @Getter
    private static Map<UUID, Member> user;

    public Synchronisation() {
        synchronisation = new HashMap<>();
        user = new HashMap<>();
    }

    @CommandAnnotation(
            command = { "synchronisation", "synchro", "sync" },
            description = "Commande pour être synchronisé avec votre compte discord.",
            usage = "intermonde synchronisation <user (Name#0000)|code>",
            permission = Permissions.SYNCHRONISATION
    )
    @Override
    public boolean handle(final @NotNull CommandSender commandSender, final @NotNull List<String> args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Lang.ERROR_CONSOLE.get());
            return false;
        }
        final Player player = (Player) commandSender;
        if (Config.getChannelId().equals("") || Config.getServerId().equals("")) {
            commandSender.sendMessage(Lang.ERROR_CONFIG.get());
            return false;
        }
        if (synchronisation.containsKey(player.getUniqueId())) {
            final String code = args.get(0);
            if (synchronisation.get(player.getUniqueId()).equals(code)) {
                DBConnection.sql.modifyQuery("INSERT INTO `players` (`uuid`, `discordId`, `hide`) VALUES ('" + player.getUniqueId() + "', '" + user.get(player.getUniqueId()).getId() + "', 'false');");
                synchronisation.remove(player.getUniqueId());
                user.remove(player.getUniqueId());
                player.sendMessage(Lang.INFO_LINK.get());
                return true;
            }
            player.sendMessage(Lang.ERROR_LINK.get());
            synchronisation.remove(player.getUniqueId());
            user.remove(player.getUniqueId());
            return false;
        }
        else {
            if (!InterMonde.getDiscordIdFromUUID(player.getUniqueId()).equals("")) {
                player.sendMessage(Lang.ERROR_MINECRAFT_LINK.get());
                return false;
            }
            new Thread(() -> {
                final String name = args.get(0).split("#")[0];
                final String disciminator = args.get(0).split("#")[1];
                System.err.println(name);
                System.err.println(disciminator);
                System.err.println(Config.getServerId());
                System.err.println();
                final Member member = InterMonde.getInstance().getJda().getGuildById(Config.getServerId()).getMemberByTag(name, disciminator);
                System.err.println(member);
                if (member != null) {
                    if (!InterMonde.getUUIDFromDiscordId(member.getId()).equals("")) {
                        player.sendMessage(Lang.ERROR_DISCORD_LINK.get());
                        return;
                    }
                    final String code = getCode();
                    try {
                        final PrivateChannel privateChannel = member.getUser().openPrivateChannel().complete();
                        privateChannel.sendMessage(Lang.INFO_DISCORD_SYNC_CODE.get(player.getName(), code)).complete();
                        privateChannel.close().queue();
                    }
                    catch (Exception e) {
                        player.sendMessage(Lang.ERROR_MESSAGE.get());
                        return;
                    }
                    player.sendMessage(Lang.INFO_MINECRAFT_SYNC.get());
                    synchronisation.put(player.getUniqueId(), code);
                    Synchronisation.user.put(player.getUniqueId(), member);
                    return;
                }
                player.sendMessage(Lang.ERROR_MESSAGE.get());
            }).start();
            return true;
        }
    }

    private String getCode() {
        final String[] dataSet = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                stringBuilder.append(dataSet[new Random().nextInt(dataSet.length)]);
            }
            if (i == 2) continue;
            stringBuilder.append("-");
        }
        return stringBuilder.toString();
    }
}
