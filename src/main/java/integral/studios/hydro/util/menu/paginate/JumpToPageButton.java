package integral.studios.hydro.util.menu.paginate;

import integral.studios.hydro.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class JumpToPageButton extends Button {

    private int page;
    private PaginatedMenu menu;
    private boolean current;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack itemStack = new ItemStack(current ? Material.ENCHANTED_BOOK : Material.BOOK, page);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.YELLOW + "Page " + page);

        if (current) {
            itemMeta.setLore(Arrays.asList("", ChatColor.GREEN + "Current page"));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return true;
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
        menu.modPage(player, page - menu.getPage());
        Button.playNeutral(player);
    }
}
