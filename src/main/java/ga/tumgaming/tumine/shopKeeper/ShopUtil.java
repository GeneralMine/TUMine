package ga.tumgaming.tumine.shopKeeper;

import ga.tumgaming.tumine.TUMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopUtil {

    /*
    Src: https://www.spigotmc.org/wiki/recipe-example/
     */

    public static void addShopmerald() {
        // Our custom variable which we will be changing around.
        ItemStack item = new ItemStack(Material.EMERALD);

        // The meta of the diamond sword where we can change the name, and properties of the item.
        ItemMeta meta = item.getItemMeta();

        // We will initialise the next variable after changing the properties of the sword

        // This sets the name of the item.
        meta.setDisplayName("§aShopmerald");

        // Set the meta of the sword to the edited meta.
        item.setItemMeta(meta);

        // create a NamespacedKey for your recipe
        NamespacedKey key = new NamespacedKey(TUMain.getPlugin(), "shopmerald");

        // Create our custom recipe variable
        ShapedRecipe recipe = new ShapedRecipe(key, item);

        // Here we will set the places. E and S can represent anything, and the letters can be anything. Beware; this is case sensitive.
        recipe.shape(" D ", "DED", " D ");

        // Set what the letters represent.
        // E = EMERALD_BLOCK, D = DIAMOND
        //
        // Recipe Shape
        // [ ][D][ ]
        // [D][E][D]
        // [ ][D][ ]
        recipe.setIngredient('E', Material.EMERALD_BLOCK);
        recipe.setIngredient('D', Material.DIAMOND);

        // Add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);
    }
}
