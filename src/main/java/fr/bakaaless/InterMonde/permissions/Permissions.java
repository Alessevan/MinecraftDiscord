package fr.bakaaless.InterMonde.permissions;

import fr.bakaaless.InterMonde.plugin.InterMonde;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum Permissions {

    NONE("", false),
    ADMIN("admin", "intermonde.admin", true),
    SYNCHRONISATION("synchronisation", "intermonde.synchronisation", true),
    HIDE("hide", "intermonde.hide", true),
    SHOW("show", "intermonde.show", true),
    EXPORT("export", "intermonde.export", true),
    IMPORT("import", "intermonde.import", true);

    @Getter(AccessLevel.PRIVATE)
    private final String path;
    @Getter(AccessLevel.PRIVATE)
    private String permission;
    @Getter(AccessLevel.PRIVATE)
    private boolean updated;

    Permissions(final String permission, final boolean updated) {
        this.path = "";
        this.permission = permission;
        this.updated = updated;
    }

    Permissions(final String path, final String permission, final boolean updated) {
        this.path = path;
        this.permission = permission;
        this.updated = updated;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String get() {
        return this.permission;
    }

    @Override
    public String toString() {
        return permission;
    }

    public boolean hasPermission(final CommandSender commandSender) {
        return hasPermission(commandSender, this);
    }

    public static boolean hasPermission(final CommandSender commandSender, final Permissions... permissions) {
        if (commandSender.hasPermission(Permissions.ADMIN.get()))
            return true;
        for (final Permissions permission : permissions) {
            if (!commandSender.hasPermission(permission.get()))
                return false;
        }
        return true;
    }

    public static void init() {
        final File file = new File(InterMonde.getInstance().getDataFolder(), "permissions.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                for (final Permissions permission : values()) {
                    if (!permission.updated) continue;
                    yamlConfiguration.set(permission.getPath(), permission.getPermission());
                }
                yamlConfiguration.save(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                for (final Permissions permission : values()) {
                    if (!permission.updated) continue;
                    if (yamlConfiguration.get(permission.getPath()) == null)
                        yamlConfiguration.set(permission.getPath(), permission.getPermission());
                    else
                        permission.setPermission(yamlConfiguration.getString(permission.getPath()));
                }
                yamlConfiguration.save(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
}
