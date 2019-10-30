package ga.tumgaming.tumine.shopKeeper;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InteractListener implements Listener {

    private static final String name = "§aShopmerald";

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getBlockFace() != BlockFace.UP) return;

        ItemStack itemStack = event.getItem();
        if(!itemStack.hasItemMeta()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName()) return;
        String displayName = itemMeta.getDisplayName();

        if(!displayName.equals(name)) return;

        Location location = player.getLocation();

        /*
        todo summon Villager, replace standart GUI with Custom Inventories

        todo add Inventories to File via JSON? YML? SQL? MONGODB?
         */
    }
}
