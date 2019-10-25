package ga.tum-gaming.tumine;

import ga.tum-gaming.tumine.listeners.ChatListener;
import ga.tum-gaming.tumine.listeners.JoinListener;
import ga.tum-gaming.tumine.listeners.QuitListener;
import ga.tum-gaming.tumine.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SpellsMain extends JavaPlugin {

    private static Config ranks;

    private static Plugin plugin;

    @Override
    public void onEnable() {
        this.plugin = this;

        ranks = new Config(this, "ranks");

        registerEvents();

        log("Plugin erfolgreich geladen");
    }

    /**
     * logs a String in the console
     *
     * @param str logged String
     */
    public void log(String str) {
        Logger.getLogger(str);
    }

    private static void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new JoinListener(ranks), plugin);
        pluginManager.registerEvents(new QuitListener(), plugin);
        pluginManager.registerEvents(new ChatListener(ranks), plugin);
    }

    public static Config getRanksConfig() {
        return ranks;
    }

}
