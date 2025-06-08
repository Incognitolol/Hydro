package integral.studios.hydro.model.listener.packetevents;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.type.RotationCheck;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.service.PlayerDataService;
import integral.studios.hydro.util.packet.PacketUtil;
import org.bukkit.entity.Player;

public class PacketEventsPacketListener extends PacketListenerAbstract {
    private final PlayerDataService playerDataService = Hydro.get(PlayerDataService.class);

    public PacketEventsPacketListener() {
        super(PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = (Player) event.getPlayer();

        if (player == null) {
            return;
        }

        PlayerData playerData = playerDataService.getData(player);

        if (playerData == null) {
            return;
        }

        for (Tracker tracker : playerDataService.getData(player).getTrackers()) {
            tracker.registerIncomingPreHandler(event);
        }

        for (PacketCheck check : playerDataService.getData(player).getCheckData().getPacketChecks()) {
            if (check.isEnabled()) {
                check.handle(event);
            }
        }

        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            if (playerFlying.hasPositionChanged() && !playerData.getTeleportTracker().isTeleporting()) {
                for (PositionCheck check : playerData.getCheckData().getPositionChecks()) {
                    if (check.isEnabled()) {
                        check.handle();
                    }
                }

                if (playerData.getTeleportTracker().getSinceTeleportTicks() > 1) {
                    playerData.getEmulationEngine().process();
                }
            }

            if (playerFlying.hasRotationChanged() && !playerData.getTeleportTracker().isTeleporting()) {
                for (RotationCheck check : playerData.getCheckData().getRotationChecks()) {
                    if (check.isEnabled()) {
                        check.handle();
                    }
                }
            }
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

        PlayerData playerData = playerDataService.getData(player);

        if (playerData == null) {
            return;
        }

        for (Tracker tracker : playerData.getTrackers()) {
            tracker.registerOutgoingPreHandler(event);
        }

        for (Tracker tracker : playerData.getTrackers()) {
            tracker.registerOutgoingPostHandler(event);
        }
    }
}