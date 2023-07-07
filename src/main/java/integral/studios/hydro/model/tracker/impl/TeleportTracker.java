package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.packet.PacketHelper;
import integral.studios.hydro.util.task.ServerTickTask;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeleportTracker extends Tracker {
    private final List<Vector> teleports;

    private int sinceTeleportTicks, lastServerPosition, lastBukkitTeleport;

    private boolean teleporting;

    public TeleportTracker(PlayerData playerData) {
        super(playerData);

        teleports = new ArrayList<>();
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            WrapperPlayServerPlayerPositionAndLook playerPositionAndLook = new WrapperPlayServerPlayerPositionAndLook(event);

            playerData.getTransactionTracker().confirmPre(() -> {
                Vector teleport = new Vector(playerPositionAndLook.getX(), playerPositionAndLook.getY(), playerPositionAndLook.getZ());

                teleports.add(teleport);
                lastServerPosition = Hydro.get(ServerTickTask.class).getTicks();
                sinceTeleportTicks = 0;
            });
        }
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            if (playerFlying.hasRotationChanged()) {
                ++sinceTeleportTicks;

                teleporting = !teleports.isEmpty();

                teleports.removeIf(teleportVector ->
                        teleportVector.getX() == playerFlying.getLocation().getX()
                                && teleportVector.getY() == playerFlying.getLocation().getY()
                                && teleportVector.getZ() == playerFlying.getLocation().getZ()
                );

                if (!teleports.isEmpty() && Hydro.get(ServerTickTask.class).getTicks() - lastServerPosition > 10) {
                    teleports.clear();
                }
            }
        }
    }
}