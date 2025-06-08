package integral.studios.hydro.model.gui.impl;

import com.samjakob.spigui.GuiManager;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.model.gui.impl.check.CheckGUI;
import integral.studios.hydro.model.gui.impl.misc.ManageGUI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * @author Salers
 * made on fr.salers.trip.gui.impl
 */
public class MainGUI implements IGUI {
    @Override
    public void openToPlayer(Player player) {
        SGMenu menu = Hydro.get(GuiManager.class).create(CC.PRI + CC.BOLD + "Hydro Anticheat", 3).setAutomaticPaginationEnabled(false);

        SGButton manageGuiButton = new SGButton(
                new ItemBuilder(Material.REDSTONE).
                        name("&c&lManage the AntiCheat").
                        enchant(Enchantment.PROTECTION_FALL, 1).flag(ItemFlag.HIDE_ENCHANTS).build())
                .withListener((InventoryClickEvent event) ->
                        getGuiService().getByClass(ManageGUI.class).openToPlayer(player)
                );

        SGButton checksGuiButton = new SGButton(
                new ItemBuilder(Material.ENCHANTED_BOOK).
                        name("&6&lManage the Checks").
                        flag(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                        .build())
                .withListener((InventoryClickEvent event) ->
                        getGuiService().getByClass(CheckGUI.class).openToPlayer(player)
                );

        menu.setButton(11, manageGuiButton);
        menu.setButton(15, checksGuiButton);

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