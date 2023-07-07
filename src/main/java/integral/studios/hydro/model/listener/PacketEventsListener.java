package integral.studios.hydro.model.listener;

import com.github.retrooper.packetevents.event.*;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.service.PlayerDataService;
import org.bukkit.entity.Player;

public class PacketEventsListener extends PacketListenerAbstract {
    private final PlayerDataService playerDataService = Hydro.get(PlayerDataService.class);

    public PacketEventsListener() {
        super(PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = (Player) event.getPlayer();

        if (player == null) {
            return;
        }

        for (Tracker tracker : playerDataService.getData(player).getTrackers()) {
            tracker.registerIncomingPreHandler(event);
        }

        for (PacketCheck check : playerDataService.getData(player).getCheckData().getPacketChecks()) {
            check.handle(event);
        }

        for (Tracker tracker : playerDataService.getData(player).getTrackers()) {
            tracker.registerIncomingPostHandler(event);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = (Player) event.getPlayer();

        if (player == null) {
            return;
        }

        for (Tracker tracker : playerDataService.getData(player).getTrackers()) {
            tracker.registerOutgoingPreHandler(event);
        }

        for (Tracker tracker : playerDataService.getData(player).getTrackers()) {
            tracker.registerOutgoingPostHandler(event);
        }
    }
}