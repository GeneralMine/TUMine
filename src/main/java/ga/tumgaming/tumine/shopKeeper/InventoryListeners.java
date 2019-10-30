package ga.tumgaming.tumine.shopKeeper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // TODO: 30.10.19 if Clicked Item is cointained in custom Shop Inventory do STUFF
        /*
        > open another Inventory
        > change Offers?
        > Claim Payment
        > Add Storage
        > handle Purchase
         */
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String inventoryTitle = event.getView().getTitle();
        // TODO: 30.10.19 if inventory equals custom Shop inventories save to DB
    }
}
