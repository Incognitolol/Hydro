package integral.studios.hydro.util.menu.button;

import integral.studios.hydro.util.math.Callback;
import integral.studios.hydro.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.ConstructorProperties;
import java.util.List;

public class BooleanButton extends Button {

    private boolean confirm;
    private Callback<Boolean> callback;

    @ConstructorProperties({"confirm", "callback"})
    public BooleanButton(boolean confirm, Callback<Boolean> callback) {
        this.confirm = confirm;
        this.callback = callback;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = new ItemStack(Material.WOOL, 1, (byte) (this.confirm ? 5 : 14));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(confirm ? "§aConfirm" : "§cCancel");
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
        if (confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0f, 0.1f);
        }

        player.closeInventory();
        callback.callback(confirm);
    }
}
