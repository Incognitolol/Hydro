package integral.studios.hydro.util.menu.menus;

import com.google.common.collect.Maps;
import integral.studios.hydro.util.math.Callback;
import integral.studios.hydro.util.menu.Button;
import integral.studios.hydro.util.menu.button.BooleanButton;
import integral.studios.hydro.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {

    private String title;
    private Callback<Boolean> response;

    public ConfirmMenu(String title, Callback<Boolean> response) {
        this.title = title;
        this.response = response;
    }

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final HashMap<Integer, Button> buttons = Maps.newHashMap();
        for (int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, response));
            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, response));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, new String[0]));
            }
        }
        return buttons;
    }
}
