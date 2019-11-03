package ga.tumgaming.tumine.shopKeeper;

import ga.tumgaming.tumine.TUMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryListeners implements Listener {

    private static HashMap<Player, Villager> statusMap = new HashMap<>();

    private static Inventory edit;

    public InventoryListeners() {
        edit = Bukkit.createInventory(null, 9, "§6Edit");
        edit.setItem(2, createItem(Material.REDSTONE_BLOCK, "§cConfig"));
        edit.setItem(4, createItem(Material.CHEST, "§eStorage"));
        edit.setItem(6, createItem(Material.DIAMOND_BLOCK, "§aPayment"));
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(entity instanceof Villager) {
            Villager villager = (Villager) entity;
            if(villager.getCustomName().equalsIgnoreCase("§6Shopkeeper")) {
                if(statusMap.containsKey(player)) statusMap.replace(player, villager);
                else statusMap.put(player, (Villager) entity);
                event.setCancelled(true);
                if(isOwner(player, villager)) {
                    player.openInventory(edit);
                } else {
                    player.openInventory(buildInventory("offers", villager));
                }
            }
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String inventoryTitle = event.getView().getTitle();

        if(inventoryTitle.equalsIgnoreCase("§6Edit")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cConfig")) player.openInventory(buildInventory("config", statusMap.get(player)));
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eStorage")) player.openInventory(buildInventory("storage", statusMap.get(player)));
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aPayment")) player.openInventory(buildInventory("payment", statusMap.get(player)));
        }
        else if(inventoryTitle.equalsIgnoreCase("§cConfig")) {
            event.setCancelled(true);
            if(event.getCurrentItem() != null || event.getCurrentItem().getType().equals(Material.AIR)) return;
            //todo
        }
        else if(inventoryTitle.equalsIgnoreCase("§eStorage")) {
            event.setCancelled(true);
            if(event.getCurrentItem() != null || event.getCurrentItem().getType().equals(Material.AIR)) return;
            //todo
        }
        else if(inventoryTitle.equalsIgnoreCase("§aPayment")) {
            event.setCancelled(true);
            if(event.getCurrentItem() != null || event.getCurrentItem().getType().equals(Material.AIR)) return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // TODO: 30.10.19 if inventory equals custom Shop inventories save to FILE
    }

    private static boolean isOwner(Player player, Villager villager) {
        String owner = TUMain.getShopsConfig().get(villager.getUniqueId().toString() + ".owner");
        return owner.equalsIgnoreCase(player.getUniqueId().toString());
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private static Inventory buildInventory(String name, Villager villager) {
        ArrayList<ItemStack> itemStacks = TUMain.getShopsConfig().get(villager.getUniqueId().toString() + "." + name);
        if(itemStacks == null) {
            itemStacks = new ArrayList<>();
        }
        String title = TUMain.getShopsConfig().get(villager.getUniqueId().toString() + "." + name + "Title");
        Inventory inventory = Bukkit.createInventory(villager, 9 * 3, title);
        for(ItemStack itemStack : itemStacks) {
            if(itemStack == null || itemStack.getType() == Material.AIR) inventory.addItem(new ItemStack(Material.AIR));
            else inventory.addItem(itemStack);
        }
        return inventory;
    }

}