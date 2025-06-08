package integral.studios.hydro.model.listener.packetevents;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.service.PlayerDataService;
import integral.studios.hydro.service.ViolationService;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PacketEventsJoinQuitListener extends PacketListenerAbstract {
    private final PlayerDataService playerDataService = Hydro.get(PlayerDataService.class);

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            playerDataService.registerData(event.getUser());
        }
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
        if (event.getUser().getConnectionState() == ConnectionState.PLAY) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        Player player = (Player) event.getPlayer();

        Set<UUID> alertsEnabled = Hydro.get(ViolationService.class).getAlertsEnabled();

        UUID uuid = player.getUniqueId();

        if (player.isOp()) {
            alertsEnabled.add(uuid);
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        playerDataService.removeData(event.getUser().getUUID());
    }
}