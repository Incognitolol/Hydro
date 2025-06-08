package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.packet.PacketUtil;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
public class VelocityTracker extends Tracker {
    private final Deque<Vector3d> possibleVelocities = new ArrayDeque<>();

    private int ticksSinceVelocity = 1000;

    public VelocityTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            ticksSinceVelocity++;
        }
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrapperPlayServerEntityVelocity entityVelocity = new WrapperPlayServerEntityVelocity(event);

            if (entityVelocity.getEntityId() == playerData.getEntityId()) {
                playerData.getTransactionTracker().confirmPre(() -> {
                    double x = entityVelocity.getVelocity().x;
                    double y = entityVelocity.getVelocity().y;
                    double z = entityVelocity.getVelocity().z;

                    possibleVelocities.add(new Vector3d(x, y, z));
                    ticksSinceVelocity = 0;
                });

                playerData.getTransactionTracker().confirmPost(() -> {
                    if (possibleVelocities.size() > 1) {
                        Vector3d velocity = possibleVelocities.peekLast();

                        possibleVelocities.clear();
                        possibleVelocities.add(velocity);
                    }
                });
            }
        }
    }

    public Vector3d peekVelocity() {
        return this.possibleVelocities.peekLast();
    }

    public boolean isOnFirstTick() {
        return ticksSinceVelocity <= 1;
    }
}