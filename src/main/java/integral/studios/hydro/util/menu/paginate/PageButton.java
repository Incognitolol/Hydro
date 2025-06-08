package integral.studios.hydro.util.menu.paginate;

import integral.studios.hydro.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@AllArgsConstructor
public class PageButton extends Button {

    private int mod;
    private PaginatedMenu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = new ItemStack(Material.CARPET);
        ItemMeta itemMeta = item.getItemMeta();

        if (hasNext(player)) {
            itemMeta.setDisplayName(mod > 0 ? ChatColor.GREEN + "Next page" : ChatColor.RED + "Previous page");
        } else {
            itemMeta.setDisplayName(ChatColor.GRAY + (mod > 0 ? "Last page" : "First page"));
        }

        item.setDurability((byte) (hasNext(player) ? 11 : 7));
        item.setItemMeta(itemMeta);

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
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(menu).openMenu(player);
            Button.playNeutral(player);
        } else {
            if (hasNext(player)) {
                menu.modPage(player, mod);
                Button.playNeutral(player);
            } else {
                Button.playFail(player);
            }
        }
    }

    @Override
    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return true;
    }

    private boolean hasNext(Player player) {
        int pg = menu.getPage() + mod;
        return pg > 0 && menu.getPages(player) >= pg;
    }
}
