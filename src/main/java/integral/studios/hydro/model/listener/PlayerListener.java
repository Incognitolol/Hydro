package integral.studios.hydro.model.listener;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.service.PlayerDataService;
import integral.studios.hydro.service.ViolationService;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final PlayerDataService playerDataService = Hydro.get(PlayerDataService.class);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Set<UUID> alertsEnabled = Hydro.get(ViolationService.class).getAlertsEnabled();

        UUID uuid = player.getUniqueId();

        if (player.isOp()) {
            if (!alertsEnabled.remove(uuid)) {
                alertsEnabled.add(uuid);
            }
        }

        playerDataService.registerData(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playerDataService.removeData(player);
    }
}