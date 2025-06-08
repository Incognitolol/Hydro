package integral.studios.hydro.util.menu.paginate;

import com.google.common.collect.Maps;
import integral.studios.hydro.util.menu.Button;
import integral.studios.hydro.util.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {

    @Getter
    public int page = 1;

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player) + " - " + this.page + "/" + this.getPages(player);
    }

    /**
     * Changes the page number
     *
     * @param player player viewing the inventory
     * @param mod delta to modify the page number by
     */
    public void modPage(Player player, int mod) {
        page += mod;
        getButtons().clear();
        openMenu(player);
    }

    /**
     * Gets the pages from a player
     *
     * @param player player viewing the inventory
     * @return The
     */
    public int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage(player));
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage(player));
        int maxIndex = (int) ((double) (page) * getMaxItemsPerPage(player));
        int topIndex = 0;

        HashMap<Integer, Button> buttons = Maps.newHashMap();

        for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();

            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) ((double) (getMaxItemsPerPage(player)) * (page - 1)) - 9;
                buttons.put(ind, entry.getValue());

                if (ind > topIndex) {
                    topIndex = ind;
                }
            }
        }

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        Map<Integer, Button> global = getGlobalButtons(player);

        if (global != null) {
            for (Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }
        }

        return buttons;
    }

    public int getMaxItemsPerPage(Player player) {
        return 27;
    }

    /**
     * @param player player viewing the inventory
     * @return a Map of button that returns items which will be present on all
     * pages
     */
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    public abstract String getPrePaginatedTitle(Player player);

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    public abstract Map<Integer, Button> getAllPagesButtons(Player player);

}
