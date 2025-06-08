package integral.studios.hydro.model.gui.impl.check.misc.impl;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.service.CheckService;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.config.ConfigurationService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @author Salers
 * made on integral.studios.hydro.model.gui.impl.check.misc.impl
 */
public class BadPacketsGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        SGMenu menu = Hydro.get(GuiManager.class).create(CC.PRI + "Bad Packets Checks", 5);
        
        for (String check : Hydro.get(CheckService.class).getAlphabeticallySortedChecks()) {
            if (!check.contains("BadPackets")) {
                continue;
            }

            boolean enabled = Hydro.get(ConfigurationService.class).isEnabled(check);
            
            SGButton button = new SGButton(new ItemBuilder(enabled ? Material.MAP : Material.EMPTY_MAP).
                    name(CC.PRI + check + "&7: " + (enabled ? "&aEnabled" : "&cDisabled")).build())
                    .withListener((InventoryClickEvent event) -> {
                        Hydro.get(ConfigurationService.class).toggleCheckActivation(check);
                        openToPlayer(player);
                    });
            
            menu.addButton(button);
        }

        player.openInventory(menu.getInventory());
    }
}