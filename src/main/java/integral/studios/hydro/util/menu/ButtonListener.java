package integral.studios.hydro.util.menu;

import integral.studios.hydro.Hydro;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ButtonListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                event.setCancelled(openMenu.isCancelClick());
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                }
                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Button button = openMenu.getButtons().get(event.getSlot());
                event.setCancelled(button.shouldCancel(player, event.getSlot(), event.getClick()));
                button.clicked(player, event.getSlot(), event.getClick());

                if (Menu.currentlyOpenedMenus.containsKey(player.getUniqueId())) {
                    Menu newMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
                    if (newMenu == openMenu && button.shouldUpdate(player, event.getSlot(), event.getClick())) {
                        newMenu.openMenu(player);
                    }
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(Hydro.get(), player::updateInventory, 1L);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        Menu menu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
        if (menu != null) {
            menu.onClose(player);
            Menu.cancelCheck(player);
            Menu.currentlyOpenedMenus.remove(player.getUniqueId());
        }
    }
}
