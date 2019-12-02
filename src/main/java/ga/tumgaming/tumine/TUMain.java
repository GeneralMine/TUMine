package ga.tumgaming.tumine;

import ga.tumgaming.tumine.autoupdater.UpdateCommand;
import ga.tumgaming.tumine.autoupdater.UpdateMethod;
import ga.tumgaming.tumine.listeners.ChatListener;
import ga.tumgaming.tumine.listeners.JoinListener;
import ga.tumgaming.tumine.listeners.QuitListener;
import ga.tumgaming.tumine.listeners.SleepListener;
import ga.tumgaming.tumine.shopKeeper.InteractListener;
import ga.tumgaming.tumine.shopKeeper.InventoryListeners;
import ga.tumgaming.tumine.shopKeeper.ShopUtil;
import ga.tumgaming.tumine.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class TUMain extends JavaPlugin {

    private static Config ranks;

    private static Config shops;

    private static Plugin plugin;

    @Override
    public void onEnable() {
        this.plugin = this;

        ranks = new Config(this, "ranks");
        shops = new Config(this, "shopkeeper");

        registerEvents();

        ShopUtil.addShopmerald();

        // Adding the update command
        this.getCommand("update").setExecutor(new UpdateCommand(
                "tumine",   // how the plugin name starts has to be the same and unique (without version)
                1000,       // plugin will reload after 1000ms
                UpdateMethod.OVERRIDE   // can be changed in the future
        ));

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
        pluginManager.registerEvents(new SleepListener(), plugin);

        pluginManager.registerEvents(new InteractListener(shops), plugin);
        pluginManager.registerEvents(new InventoryListeners(), plugin);
    }

    public static Config getRanksConfig() {
        return ranks;
    }

    public static Config getShopsConfig() {
        return shops;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

}
