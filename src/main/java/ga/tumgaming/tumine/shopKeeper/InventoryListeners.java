package ga.tumgaming.tumine.shopKeeper;

import ga.tumgaming.tumine.TUMain;
import ga.tumgaming.tumine.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

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
                    player.openInventory(generateOffers(buildInventory("config", villager)));
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
        Inventory inventory = event.getInventory();

        if(!statusMap.containsKey(player)) return;
        Villager villager = statusMap.get(player);
        if(inventoryTitle.equalsIgnoreCase("§6Edit")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cConfig")) player.openInventory(buildInventory("config", statusMap.get(player)));
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eStorage")) player.openInventory(buildInventory("storage", statusMap.get(player)));
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aPayment")) player.openInventory(buildInventory("payment", statusMap.get(player)));
        }
        else if(inventoryTitle.equalsIgnoreCase("§cConfig")) {
            if(Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
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
        else if(inventoryTitle.equalsIgnoreCase("§eOffers")) {
            event.setCancelled(true);

            if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
            ItemStack itemStack = event.getCurrentItem();
            if(!itemStack.hasItemMeta()) return;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(!itemMeta.hasDisplayName()) return;
            String displayName = itemMeta.getDisplayName();
            if(!displayName.startsWith("§aPurchase Offer #")) return;

            //check if Buyer inventory is full
            if(isFull(player.getInventory().getStorageContents())) {
                player.sendMessage("§8>> §7Cant handle purchase due to full player inventory");
                return;
            }

            //useful stuff
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            int current = Integer.parseInt(name.substring(name.length() - 1));
            ItemStack purchasedItem = inventory.getItem(current - 1);
            ItemStack price = inventory.getItem(current - 1 + 18);
            Inventory storage = buildInventory("storage", villager);

            //check if Shop is stocked
            if(!storage.containsAtLeast(purchasedItem, purchasedItem.getAmount())) {
                player.sendMessage("§8>> §7Shop is not stocked please contact §e" + getOwner(villager) + " §7to restock it");
                return;
            }

            //check if Payment is not full
            Inventory payment = buildInventory("payment", villager);
            if(isFull(payment)) {
                player.sendMessage("§8>> §7The shops cashbox is full please contact §e"+ getOwner(villager) + " §7to empty it");
                return;
            }

            //check if Player has Payment
            if(!player.getInventory().containsAtLeast(price, price.getAmount())) {
                player.sendMessage("§8>> §7Im sorry! You dont have enough of the required items to make this purchase");
                return;
            }

            removeItems(storage, purchasedItem.getType(), purchasedItem.getAmount());
            payment.addItem(price);
            removeItems(player.getInventory(), price.getType(), price.getAmount());
            player.getInventory().addItem(purchasedItem);
            Config shops = TUMain.getShopsConfig();
            shops.set(villager.getUniqueId().toString() + ".storage", storage.getContents());
            shops.set(villager.getUniqueId().toString() + ".payment", payment.getContents());
            shops.reload();

            player.sendMessage("§8>> §7You successfully purchased §a" + purchasedItem.getAmount() + " " + purchasedItem.getType().toString().toLowerCase() + "§7 for §c" + price.getAmount() + " " + price.getType().toString().toLowerCase());
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

    private Inventory generateOffers(Inventory config) {
        Inventory offers = Bukkit.createInventory(null, 9 * 3, "§eOffers");

        for(int i = 0; i < 9; i++) {
            ItemStack top = config.getItem(i);
            ItemStack bottom = config.getItem(i + 18);

            if((top == null || top.getType().equals(Material.AIR)) && (bottom == null || bottom.getType().equals(Material.AIR))) {
                offers.setItem(i, new ItemStack(Material.AIR));
                offers.setItem(i + 9, createItem(Material.RED_STAINED_GLASS_PANE, "§cEmpty Offer"));
                offers.setItem(i + 18, new ItemStack(Material.AIR));
            } else if((top == null || top.getType().equals(Material.AIR)) || (bottom == null || bottom.getType().equals(Material.AIR))) {
                offers.setItem(i, new ItemStack(Material.AIR));
                offers.setItem(i + 9, createItem(Material.RED_STAINED_GLASS_PANE, "§cEmpty Offer"));
                offers.setItem(i + 18, new ItemStack(Material.AIR));
            } else if((top != null || !top.getType().equals(Material.AIR)) && (bottom != null || !bottom.getType().equals(Material.AIR))) {
                offers.setItem(i, top);
                offers.setItem(i + 9, createItem(Material.LIME_STAINED_GLASS_PANE, "§aPurchase Offer #" + (i+1)));
                offers.setItem(i + 18, bottom);
            }
        }

        return offers;
    }

    /**
     * Checks if a given inventory is full
     * @param inventory the given inventory
     * @return if its full
     */
    private static boolean isFull(Inventory inventory) {
         boolean isfull = true;
         for(int i = 0; i < inventory.getSize(); i++) {
             ItemStack current = inventory.getItem(i);
             if(current == null || current.getType().equals(Material.AIR)) {
                 isfull = false;
                 break;
             }
         }
         return isfull;
    }

    /**
     * Checks if an given ItemStackArray is full
     * @param itemStacks the given array
     * @return if its full
     */
    private static boolean isFull(ItemStack[] itemStacks) {
        boolean isfull = true;
        for(int i = 0; i < itemStacks.length; i++) {
            ItemStack current = itemStacks[i];
            if(current == null || current.getType().equals(Material.AIR)) {
                isfull = false;
                break;
            }
        }
        return isfull;
    }

    /*
    Credits to:
    https://bukkit.org/members/blablubbabc.64583/
    Src found:
    https://bukkit.org/threads/how-do-i-remove-a-specific-amount-of-items-from-inventory.312565/
     */
    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
        ItemStack is = inventory.getItem(slot);
        if (is == null) continue;
        if (type == is.getType()) {
        int newAmount = is.getAmount() - amount;
        if (newAmount > 0) {
        is.setAmount(newAmount);
        break;
        } else {
        inventory.clear(slot);
        amount = -newAmount;
        if (amount == 0) break;
        }
        }
        }
        }

private static String getOwner(Villager villager) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(TUMain.getShopsConfig().get(villager.getUniqueId().toString() + ".owner")));
        return player.getName();
        }
        }