package integral.studios.hydro.model.gui;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.service.GuiService;
import org.bukkit.entity.Player;

public interface IGUI {
    void openToPlayer(final Player player);

    default GuiService getGuiService() {
        return Hydro.get(GuiService.class);
    }
}