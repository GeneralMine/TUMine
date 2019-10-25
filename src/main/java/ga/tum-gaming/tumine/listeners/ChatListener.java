package ga.tum-gaming.tumine.listeners;

import ga.tum-gaming.tumine.util.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private Config ranks;

    public ChatListener(Config ranks) {
        this.ranks = ranks;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        //Default Bukkit Chat Format.
        //event.setFormat(ChatColor.WHITE + "<" + p.getDisplayName() + ">" + event.getMessage());

        event.setFormat(ranks.get(event.getPlayer().getName()) + "§8 | §7" + event.getPlayer().getDisplayName() + "§8: " + "§f"  + event.getMessage());
    }
}
