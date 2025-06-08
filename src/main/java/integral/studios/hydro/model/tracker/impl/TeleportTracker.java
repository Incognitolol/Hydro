package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.packet.PacketUtil;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;

@Getter
public class TeleportTracker extends Tracker {
    private final ArrayDeque<Vector> teleports = new ArrayDeque<>();

    private int sinceTeleportTicks;

    private boolean teleporting;

    public TeleportTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            WrapperPlayServerPlayerPositionAndLook playerPositionAndLook = new WrapperPlayServerPlayerPositionAndLook(event);

            playerData.getTransactionTracker().confirmPre(() -> {
                Vector teleport = new Vector(playerPositionAndLook.getX(), playerPositionAndLook.getY(), playerPositionAndLook.getZ());

                teleports.add(teleport);
            });
        }
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            if (playerFlying.hasRotationChanged() && playerFlying.hasPositionChanged()) {
                ++sinceTeleportTicks;

                teleporting = false;

                if (!teleports.isEmpty()) {
                    Vector teleportPosition = teleports.peek();

                    double x = playerData.getPositionTracker().getX();
                    double y = playerData.getPositionTracker().getY();
                    double z = playerData.getPositionTracker().getZ();

                    if (Math.abs(x - teleportPosition.getX()) < 1E-8
                            && Math.abs(y - teleportPosition.getY()) < 1E-8
                            && Math.abs(z - teleportPosition.getZ()) < 1E-8) {
                        teleports.removeFirst();

                        teleporting = true;
                        sinceTeleportTicks = 0;
                    }
                }
            }
        }
    }
}