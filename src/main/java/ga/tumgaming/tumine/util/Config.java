package ga.tumgaming.tumine.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Config {

    private YamlConfiguration config;
    private File file;

    public Config(Plugin plugin, String configName) {
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

        file = new File(plugin.getDataFolder(), configName + ".yml");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String path) {
        return (T) config.get(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public void reload() {
        save();
        config = YamlConfiguration.loadConfiguration(file);
    }
}
