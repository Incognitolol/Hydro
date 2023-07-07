package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
public class VelocityTracker extends Tracker {
    private final Deque<Vector3d> possibleVelocities = new ArrayDeque<>();

    private Vector3d lastRemovedVelocity = new Vector3d(666, 666, 666);

    private int ticksSinceVelocity;

    private boolean confirmed, split;

    public VelocityTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            ticksSinceVelocity++;
        }
    }

    @Override
    public void registerIncomingPostHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            possibleVelocities.clear();

            split = confirmed;
        }
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrapperPlayServerEntityVelocity entityVelocity = new WrapperPlayServerEntityVelocity(event);

            if (entityVelocity.getEntityId() == playerData.getEntityId()) {
                playerData.getTransactionTracker().confirmPre(() -> {
                    double x = entityVelocity.getVelocity().x ;
                    double y = entityVelocity.getVelocity().y;
                    double z = entityVelocity.getVelocity().z;

                    possibleVelocities.add(new Vector3d(x,y,z));
                    ticksSinceVelocity = 0;

                    confirmed = true;
                });

                playerData.getTransactionTracker().confirmPost(() -> {
                    if (possibleVelocities.size() > 1) {
                        this.lastRemovedVelocity = possibleVelocities.removeFirst();
                    }

                    // The Mexify way
                    confirmed = false;
                });
            }
        }
    }

    public Vector3d peekVelocity() {
        return this.possibleVelocities.peekLast();
    }

    public boolean isOnFirstTick() {
        return !this.possibleVelocities.isEmpty();
    }
}