package integral.studios.hydro.util.menu.button;

import integral.studios.hydro.util.menu.Button;
import integral.studios.hydro.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BackButton extends Button {

    private Menu back;

    public BackButton(Menu back) {
        this.back = back;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = new ItemStack(Material.BED);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Go Back");
        meta.setLore(Arrays.asList("", ChatColor.RED + "Click here to return to", ChatColor.RED + "the previous menu."));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getName(Player var1) {
        return null;
    }

    @Override
    public List<String> getDescription(Player var1) {
        return null;
    }

    @Override
    public Material getMaterial(Player var1) {
        return null;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        Button.playNeutral(player);
        back.openMenu(player);
    }
}
