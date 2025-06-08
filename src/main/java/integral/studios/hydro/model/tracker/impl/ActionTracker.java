package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.packet.PacketUtil;
import lombok.Getter;

@Getter
public class ActionTracker extends Tracker {
    private int ticksSinceAttack = Integer.MAX_VALUE;
    private int lastAttackedEntityId = -1;

    private boolean digging = false;
    private boolean sprinting = false;
    private boolean sneaking = false;


    public ActionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            handlePlayerDigging(event);
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            handleInteractEntity(event);
        } else if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            handleEntityAction(event);
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            handleFlyingPacket();
        }
    }

    private void handlePlayerDigging(PacketReceiveEvent event) {
        WrapperPlayClientPlayerDigging playerDigging = new WrapperPlayClientPlayerDigging(event);
        DiggingAction action = playerDigging.getAction();

        if (action == DiggingAction.START_DIGGING) {
            digging = true;
        } else if (action == DiggingAction.CANCELLED_DIGGING) {
            digging = false;
        }
    }

    private void handleInteractEntity(PacketReceiveEvent event) {
        WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

        if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
            ticksSinceAttack = 0;
            lastAttackedEntityId = useEntity.getEntityId();
        }
    }

    private void handleEntityAction(PacketReceiveEvent event) {
        WrapperPlayClientEntityAction entityAction = new WrapperPlayClientEntityAction(event);

        switch (entityAction.getAction()) {
            case START_SPRINTING:
                sprinting = true;
                break;
            case STOP_SPRINTING:
                sprinting = false;
                break;
            case START_SNEAKING:
                sneaking = true;
                break;
            case STOP_SNEAKING:
                sneaking = false;
                break;
        }
    }

    private void handleFlyingPacket() {
        // Increment attack timer on every movement packet
        if (ticksSinceAttack < Integer.MAX_VALUE) {
            ticksSinceAttack++;
        }
    }

    public boolean hasRecentlyAttacked(int maxTicks) {
        return ticksSinceAttack <= maxTicks;
    }

    public boolean isPerformingMovementAction() {
        return sneaking || digging;
    }

    public double getMovementSpeedMultiplier() {
        if (sneaking) {
            return 0.3;
        } else if (sprinting) {
            return 1.3;
        }
        return 1.0;
    }

    public void reset() {
        ticksSinceAttack = Integer.MAX_VALUE;
        lastAttackedEntityId = -1;
        digging = false;
        sprinting = false;
        sneaking = false;
    }
}