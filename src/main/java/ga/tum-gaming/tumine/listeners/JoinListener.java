package ga.tum-gaming.tumine.listeners;

import ga.tum-gaming.tumine.util.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private Config config;

    public JoinListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("§7 Der Spieler §e" +player.getDisplayName() + "§7 hat den Server betreten");

        player.setPlayerListHeaderFooter("§b Willkommen auf §9TUM§7-§9Craft", "§bBei Problemen wende dich bitte an einen §cAdmin");

        if(config.get(player.getName()) == null) {
            String rank = player.isOp() ? "§cAdmin" : "§eSpieler";
            config.set(player.getName(), rank);
        }

        player.setPlayerListName(config.get(player.getName()) + " §7| §f" + player.getName());
    }

}
