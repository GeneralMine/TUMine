package ga.tumgaming.tumine.shopKeeper;

import ga.tumgaming.tumine.TUMain;
import ga.tumgaming.tumine.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
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
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if(damaged.getCustomName().equalsIgnoreCase("§6Shopkeeper")) {
            event.setCancelled(true);
            if(!(damager instanceof Player)) return;
            Villager villager = (Villager) damaged;
            Player player = (Player) damager;

            if(!isOwner(player, villager)) return;
            if(player.getInventory().getItemInMainHand().getType().equals(Material.LAVA_BUCKET)) {
                TUMain.getShopsConfig().set(villager.getUniqueId().toString(), null);
                villager.remove();
                player.sendMessage("§8>> §7This §6Shopkeeper §7got removed");
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
            if(event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                event.setCancelled(true);
                return;
            }
        }
        else if(inventoryTitle.equalsIgnoreCase("§eStorage")) {
            //todo
        }
        else if(inventoryTitle.equalsIgnoreCase("§aPayment")) {
            //todo
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String inventoryTitle = event.getView().getTitle();
        Config shops = TUMain.getShopsConfig();

        if(!statusMap.containsKey(player)) return;

        Villager villager = statusMap.get(player);

        if(inventoryTitle.equalsIgnoreCase("§eStorage")) {
            shops.set(villager.getUniqueId().toString() + ".storage", event.getInventory().getContents());
        }
        else if(inventoryTitle.equalsIgnoreCase("§cConfig")) {
            validate(event.getInventory(), player);
            shops.set(villager.getUniqueId().toString() + ".config", event.getInventory().getContents());
        }
        else if(inventoryTitle.equalsIgnoreCase("§aPayment")) {
            shops.set(villager.getUniqueId().toString() + ".payment", event.getInventory().getContents());
        }

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
        ArrayList<ItemStack> itemStacks = (ArrayList) TUMain.getShopsConfig().get(villager.getUniqueId().toString() + "." + name);
        ItemStack[] array = itemStacks.toArray(new ItemStack[itemStacks.size()]);
        String title = TUMain.getShopsConfig().get(villager.getUniqueId().toString() + "." + name + "Title");
        Inventory inventory = Bukkit.createInventory(villager, 9 * 3, title);
        int count = 0;
        for(ItemStack itemStack : array) {
            if(itemStack == null || itemStack.getType() == Material.AIR) inventory.addItem(new ItemStack(Material.AIR));
            else inventory.setItem(count, itemStack);

            count++;
        }
        return inventory;
    }

    private static void validate(Inventory itemStacks, Player player) {
        player.sendMessage("§7--- --- --- ---");
        for(int i = 0; i < 9; i++) {
            ItemStack top = itemStacks.getItem(i);
            ItemStack bottom = itemStacks.getItem(i + 18);

            if((top == null || top.getType().equals(Material.AIR)) && (bottom == null || bottom.getType().equals(Material.AIR))) {
                player.sendMessage("§8>> §7Offer #" + (i + 1) + "§7 --> §eEmpty");
            } else if((top == null || top.getType().equals(Material.AIR)) || (bottom == null || bottom.getType().equals(Material.AIR))) {
                player.sendMessage("§8>> §7Offer #" + (i + 1) + "§7 --> §cInvalid");
            } else if((top != null || !top.getType().equals(Material.AIR)) && (bottom != null || !bottom.getType().equals(Material.AIR))) {
                player.sendMessage("§8>> §7Offer #" + (i + 1) + "§7 --> §aValid");
            }
        }
        player.sendMessage("§7--- --- --- ---");
    }

}