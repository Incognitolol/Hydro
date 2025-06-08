package integral.studios.hydro.model.gui.impl.check.misc;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.BadPacketsGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.PingSpoofGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.PostGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.TimerGUI;
import integral.studios.hydro.util.chat.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Salers
 * made on integral.studios.hydro.model.gui.impl.check.misc
 */
public class MiscChecksGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        final SGMenu menu = Hydro.get(GuiManager.class).create("&d&lOther Checks", 3).setAutomaticPaginationEnabled(false);

        final SGButton badpacketsButton = new SGButton(
                new ItemBuilder(Material.REDSTONE).
                        name(CC.PRI + "Bad Packets Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(BadPacketsGUI.class).openToPlayer(player));

        final SGButton pingSpoofButton = new SGButton(
                new ItemBuilder(Material.PAINTING).
                        name(CC.PRI + "Ping Spoof Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(PingSpoofGUI.class).openToPlayer(player));

        final SGButton postButton = new SGButton(
                new ItemBuilder(Material.REDSTONE_COMPARATOR).
                        name(CC.PRI + "Post Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(PostGUI.class).openToPlayer(player));

        final SGButton timerButton = new SGButton(
                new ItemBuilder(Material.CARROT_STICK).
                        name(CC.PRI + "Timer Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(TimerGUI.class).openToPlayer(player));

        menu.setButton(10, badpacketsButton);
        menu.setButton(12, pingSpoofButton);
        menu.setButton(14, postButton);
        menu.setButton(16, timerButton);

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
