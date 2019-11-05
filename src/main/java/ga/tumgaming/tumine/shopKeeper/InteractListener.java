package ga.tumgaming.tumine.shopKeeper;

import ga.tumgaming.tumine.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InteractListener implements Listener {

    private static final String name = "§aShopmerald";

    private Config shops;

    private Inventory config;
    private Inventory storage;
    private Inventory payment;


    public InteractListener(Config config) {
        this.shops = config;
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getBlockFace() != BlockFace.UP) return;

        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;
        ItemStack itemStack = event.getItem();
        if(!itemStack.hasItemMeta()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName()) return;
        String displayName = itemMeta.getDisplayName();

        if(!displayName.equals(name)) return;

        Location location = event.getClickedBlock().getLocation().add(0.0, 1.0, 0.0);

        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setAdult();
        villager.setBreed(false);
        villager.setAgeLock(true);
        villager.setCanPickupItems(false);
        villager.setCustomNameVisible(true);
        villager.setCustomName("§6Shopkeeper");
        villager.setInvulnerable(true);
        villager.setCollidable(false);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255), true);

        config = Bukkit.createInventory(villager, 9 * 3, "§cConfig");
        storage = Bukkit.createInventory(villager, 9 * 3, "§eStorage");
        payment = Bukkit.createInventory(villager, 9 * 3, "§aPayment");

        shops.set(villager.getUniqueId().toString() + ".owner", player.getUniqueId().toString());
        shops.set(villager.getUniqueId().toString() + ".configTitle", "§cConfig");
        shops.set(villager.getUniqueId().toString() + ".storageTitle", "§eStorage");
        shops.set(villager.getUniqueId().toString() + ".paymentTitle", "§aPayment");
        for(int i = 9; i < 18; i++) {
            config.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, "§eOffer #" + (i-8)));
        }
        shops.set(villager.getUniqueId().toString() + ".config", config.getContents());
        shops.set(villager.getUniqueId().toString() + ".storage", storage.getContents());
        shops.set(villager.getUniqueId().toString() + ".payment", payment.getContents());

        shops.reload();

        if(itemStack.getAmount() > 1) {
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
