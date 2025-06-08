package integral.studios.hydro.model.gui.impl.check.combat;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.model.gui.impl.check.combat.impl.*;
import integral.studios.hydro.model.gui.impl.check.combat.impl.*;
import integral.studios.hydro.util.chat.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Salers
 * made on integral.studios.hydro.model.gui.impl.check.combat
 */
public class CombatChecksGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        SGMenu menu = Hydro.get(GuiManager.class).create("&b&lCombat Checks", 4).setAutomaticPaginationEnabled(false);

        SGButton aimAssistButton = new SGButton(
                new ItemBuilder(Material.FISHING_ROD).
                        name(CC.PRI + "Aim Assist Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(AimAssistGUI.class).openToPlayer(player));
        SGButton autoclickerButton = new SGButton(
                new ItemBuilder(Material.GOLD_HOE).
                        name(CC.PRI + "Auto Clicker Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(AutoClickerGUI.class).openToPlayer(player));

        SGButton killauraButton = new SGButton(
                new ItemBuilder(Material.BLAZE_ROD).
                        name(CC.PRI + "KillAura Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(KillAuraGUI.class).openToPlayer(player));

        SGButton reachButton = new SGButton(
                new ItemBuilder(Material.BOW).
                        name(CC.PRI + "Reach Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(ReachGUI.class).openToPlayer(player));

        SGButton velocityButton = new SGButton(
                new ItemBuilder(Material.ANVIL).
                        name(CC.PRI + "Velocity Checks")
                        .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(VelocityGUI.class).openToPlayer(player));


        menu.setButton(11, aimAssistButton);
        menu.setButton(13, autoclickerButton);
        menu.setButton(15, killauraButton);
        menu.setButton(21, velocityButton);
        menu.setButton(23, reachButton);

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