package integral.studios.hydro.model.gui.impl.misc;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.gui.IGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Salers
 * made on integral.studios.hydro.model.gui.impl.misc
 */
public class ManageGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        SGMenu menu = Hydro.get(GuiManager.class).create("&c&lManage", 3).setAutomaticPaginationEnabled(false);

        boolean performanceMode = Hydro.get().getConfiguration().isPerformanceMode();
        
        SGButton performanceModeButton = new SGButton(new ItemBuilder(
                performanceMode ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .name(CC.PRI + "Performance Mode : " + (performanceMode ? "&aActivated" : "&cDisabled"))
                .build()).withListener((InventoryClickEvent event) -> {
                    Hydro.get().getConfiguration().setPerformanceMode(!performanceMode);
                    player.closeInventory();
                    openToPlayer(player);
                }
        );

        menu.setButton(10, performanceModeButton);

        for (int i = 0; i < menu.getRowsPerPage() * 9; i++) {
            if (menu.getButton(i) == null) {
                SGButton glassPane = new SGButton(new ItemStack(
                        Material.STAINED_GLASS_PANE,
                        1,
                        (byte) 0
                ));

                menu.setButton(i, glassPane);
            }
        }

        player.openInventory(menu.getInventory());
    }
}