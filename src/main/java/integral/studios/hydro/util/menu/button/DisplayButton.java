package integral.studios.hydro.util.menu.button;

import integral.studios.hydro.util.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DisplayButton extends Button {

    private ItemStack item;
    private boolean cancel;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (item == null) {
            return new ItemStack(Material.AIR);
        } else {
            return item;
        }
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
    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return cancel;
    }
}
