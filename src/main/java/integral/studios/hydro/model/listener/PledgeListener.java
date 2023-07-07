package integral.studios.hydro.model.listener;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.service.PlayerDataService;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PledgeListener implements Listener {
    @EventHandler
    public void onReceive(PacketFrameReceiveEvent event) {
        Player player = event.getPlayer();

        PlayerData playerData = Hydro.get(PlayerDataService.class).getData(player);

        if (playerData != null) {
            playerData.getTransactionTracker().handleTransaction(event);
        }
    }
}