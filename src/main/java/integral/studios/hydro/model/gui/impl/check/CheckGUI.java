package integral.studios.hydro.model.gui.impl.check;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.model.gui.impl.check.combat.CombatChecksGUI;
import integral.studios.hydro.model.gui.impl.check.misc.MiscChecksGUI;
import integral.studios.hydro.model.gui.impl.check.movement.MovementChecksGUI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * @author Salers
 * made on integral.studios.hydro.model.gui.impl.check
 */
public class CheckGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        SGMenu menu = Hydro.get(GuiManager.class).create("&e&lChecks", 3).setAutomaticPaginationEnabled(false);

        SGButton combatGuiButton = new SGButton(new ItemBuilder(Material.IRON_SWORD)
                .name("&b&lCombat Checks")
                .enchant(Enchantment.PROTECTION_FALL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(CombatChecksGUI.class).openToPlayer(player));

        SGButton movementGuiButton =  new SGButton(new ItemBuilder(Material.DIAMOND_BOOTS)
                .name("&3&lMovement Checks")
                .enchant(Enchantment.PROTECTION_FALL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(MovementChecksGUI.class).openToPlayer(player));

        SGButton miscGuiButton = new SGButton(new ItemBuilder(Material.MINECART)
                .name("&d&lOther Checks")
                .enchant(Enchantment.PROTECTION_FALL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .build()
        ).withListener((InventoryClickEvent event) ->
                getGuiService().getByClass(MiscChecksGUI.class).openToPlayer(player));

        menu.setButton(11, combatGuiButton);
        menu.setButton(13, movementGuiButton);
        menu.setButton(15, miscGuiButton);

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