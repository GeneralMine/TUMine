package ga.tumgaming.tumine.shopKeeper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopKeeper {

    private Inventory offers;
    private Inventory config;
    private Inventory storage;
    private Inventory payment;
    private Inventory edit;

    private Player owner;

    private static final String configName = "§aConfig";
    private static final String storageName = "§cStorage";
    private static final String paymentName = "§bPayment";

    public ShopKeeper(Player owner) {
        this.owner = owner;
    }

    private void createInventories() {
        offers = Bukkit.createInventory(null, 9 * 3);
        config = Bukkit.createInventory(null, 9 * 3);

        storage = Bukkit.createInventory(null, 9 * 6);
        payment = Bukkit.createInventory(null, 9 * 6);

        edit = Bukkit.createInventory(null, 9);
        edit.setItem(3, createItem(Material.REDSTONE_BLOCK, configName));
        edit.setItem(5, createItem(Material.CHEST, storageName));
        edit.setItem(7, createItem(Material.DIAMOND_BLOCK, paymentName));
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
