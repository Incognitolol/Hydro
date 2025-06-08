package integral.studios.hydro.model.gui.impl.check.movement;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.FlightGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.GroundGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.MotionGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.SpeedGUI;
import integral.studios.hydro.util.chat.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Salers
 * made on integral.studios.hydro.model.gui.impl.check.movement
 */
public class MovementChecksGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        SGMenu menu = Hydro.get(GuiManager.class).create("&3&lMovement Checks", 3).setAutomaticPaginationEnabled(false);

        SGButton flightButton = new SGButton(
                new ItemBuilder(Material.FEATHER).
                        name(CC.PRI + "Flight Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(FlightGUI.class).openToPlayer(player));

        SGButton groundButton = new SGButton(
                new ItemBuilder(Material.STONE).
                        name(CC.PRI + "Ground Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(GroundGUI.class).openToPlayer(player));

        SGButton motionButton = new SGButton(
                new ItemBuilder(Material.FIREWORK_CHARGE).
                        name(CC.PRI + "Motion Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(MotionGUI.class).openToPlayer(player));

        SGButton speedButton = new SGButton(
                new ItemBuilder(Material.RABBIT_FOOT).
                        name(CC.PRI + "Speed Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(SpeedGUI.class).openToPlayer(player));

        menu.setButton(10, flightButton);
        menu.setButton(12, groundButton);
        menu.setButton(14, motionButton);
        menu.setButton(16, speedButton);

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