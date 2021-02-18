package fr.bakaaless.InterMonde.mysql;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DataBaseFile {

    private static String engine = "sqlite";
    private static String host = "localhost";
    private static String user = "user";
    private static String password = "p@ssworD";
    private static String database = "database";
    private static int port = 3306;

    public static void init(final String path) {
        final File file = new File(path, "database.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                yamlConfiguration.set("engine", engine);
                yamlConfiguration.set("host", host);
                yamlConfiguration.set("user", user);
                yamlConfiguration.set("password", password);
                yamlConfiguration.set("database", database);
                yamlConfiguration.set("port", port);
                yamlConfiguration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                final YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.load(file);
                engine = yamlConfiguration.getString("engine");
                host = yamlConfiguration.getString("host");
                user = yamlConfiguration.getString("user");
                password = yamlConfiguration.getString("password");
                database = yamlConfiguration.getString("database");
                port = yamlConfiguration.getInt("port");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getEngine() {
        return engine;
    }

    public static String getHost() {
        return host;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static String getDatabase() {
        return database;
    }

    public static int getPort() {
        return port;
    }
}
