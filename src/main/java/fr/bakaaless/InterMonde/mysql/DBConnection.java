package fr.bakaaless.InterMonde.mysql;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class DBConnection {

    private static final Map<String, String> tables = new HashMap<>();

    public static DataBase sql;

    private static String host;
    private static int port;
    private static String db;
    private static String user;
    private static String pass;
    private static boolean isOpen = false;

    public static void init(final JavaPlugin plugin) {
        DataBaseFile.init(plugin.getDataFolder().getAbsolutePath());
        host = DataBaseFile.getHost();
        port = DataBaseFile.getPort();
        db = DataBaseFile.getDatabase();
        user = DataBaseFile.getUser();
        pass = DataBaseFile.getPassword();
        tables.put("players", "CREATE TABLE `players` (" +
                "  `uuid` VARCHAR(36) PRIMARY KEY NOT NULL," +
                "  `discordId` VARCHAR(36) NOT NULL," +
                "  `hide` VARCHAR(5) NOT NULL" +
                ");");
        if (DataBaseFile.getEngine().equalsIgnoreCase("mysql")) {
            sql = new MySQL(plugin.getLogger(), "Establishing MySQL Connection...", host, port, user, pass, db);
            if (sql.open() == null) {
                plugin.getLogger().severe("Disabling due to database error");
                plugin.onDisable();
                return;
            }
            isOpen = true;
            plugin.getLogger().info("Database connection established.");
        } else {
            sql = new SQLite(plugin.getLogger(), "Establishing SQLite Connection.", "Core.db", plugin.getDataFolder().getAbsolutePath());
            if (sql.open() == null) {
                plugin.getLogger().severe("Disabling due to database error");
                plugin.onDisable();
                return;
            }
            isOpen = true;
        }
        createTable(plugin);
    }

    private static void createTable(final JavaPlugin plugin) {
        for (final String table : tables.keySet()) {
            if (!sql.tableExists(table)) {
                plugin.getLogger().info("Creating table : " + table);
                final String query = tables.get(table);
                sql.modifyQuery(query, false);
            }
        }
    }

    public static boolean isOpen() {
        return isOpen;
    }
}
