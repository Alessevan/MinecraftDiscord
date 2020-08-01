package fr.bakaaless.InterMonde.mysql;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DataBaseFile {

    @Getter
    private static String engine = "sqlite";
    @Getter
    private static String host = "localhost";
    @Getter
    private static String user = "user";
    @Getter
    private static String password = "p@ssworD";
    @Getter
    private static String database = "database";
    @Getter
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
}
