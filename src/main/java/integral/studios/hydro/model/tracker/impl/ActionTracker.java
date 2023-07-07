package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;

@Getter
public class ActionTracker extends Tracker {
    private int lastAttack;

    private boolean digging, sprinting, sneaking;

    public ActionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging playerDigging = new WrapperPlayClientPlayerDigging(event);

            switch (playerDigging.getAction()) {
                case START_DIGGING: {
                    digging = true;
                    break;
                }

                case CANCELLED_DIGGING: {
                    digging = false;
                    break;
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

            if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                lastAttack = 0;
            }
        }

        if (PacketHelper.isFlying(event.getPacketType())) {
            ++lastAttack;
        } else if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction entityAction = new WrapperPlayClientEntityAction(event);

            switch (entityAction.getAction()) {
                case START_SPRINTING: {
                    sprinting = true;
                    break;
                }

                case START_SNEAKING: {
                    sneaking = true;
                    break;
                }

                case STOP_SPRINTING: {
                    sprinting = false;
                    break;
                }

                case STOP_SNEAKING: {
                    sneaking = false;
                    break;
                }
            }
        }
    }
}