package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.nms.MovementUtil;
import lombok.Getter;

@Getter
public class AttributeTracker extends Tracker {
    private float walkSpeed = 0.1F;

    private int jumpBoost, speedBoost, slowness;

    private boolean flightAllowed, creativeMode, flying;

    public AttributeTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            WrapperPlayServerUpdateAttributes updateAttributes = new WrapperPlayServerUpdateAttributes(event);

            if (updateAttributes.getEntityId() != playerData.getEntityId()) {
                return;
            }

            for (WrapperPlayServerUpdateAttributes.Property snapshot : updateAttributes.getProperties()) {
                if (snapshot.getKey().equals("generic.movementSpeed")) {
                    playerData.getTransactionTracker().confirmPre(() -> {
                        walkSpeed = (float) MovementUtil.getMovementSpeed(snapshot.getModifiers(), (float) snapshot.getValue());
                    });

                    break;
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect entityEffect = new WrapperPlayServerEntityEffect(event);

            if (entityEffect.getEntityId() != playerData.getEntityId()) {
                return;
            }

            int amplifier = entityEffect.getEffectAmplifier();

            playerData.getTransactionTracker().confirmPre(() -> {
                switch (entityEffect.getPotionType().getId()) {
                    case 1: {
                        speedBoost = amplifier + 1;
                        break;
                    }

                    case 2: {
                        slowness = amplifier + 1;
                        break;
                    }

                    case 8: {
                        jumpBoost = amplifier + 1;
                        break;
                    }
                }
            });
        }

        if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            WrapperPlayServerRemoveEntityEffect removeEntityEffect = new WrapperPlayServerRemoveEntityEffect(event);

            if (removeEntityEffect.getEntityId() != playerData.getEntityId()) {
                return;
            }

            playerData.getTransactionTracker().confirmPre(() -> {
                switch (removeEntityEffect.getPotionType().getId()) {
                    case 1: {
                        speedBoost = 0;
                        break;
                    }

                    case 2: {
                        slowness = 0;
                        break;
                    }

                    case 8: {
                        jumpBoost = 0;
                        break;
                    }
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            WrapperPlayServerPlayerAbilities wrapper = new WrapperPlayServerPlayerAbilities(event);

            playerData.getTransactionTracker().confirmPre(() -> {
                flightAllowed = wrapper.isFlightAllowed();
                creativeMode = wrapper.isInCreativeMode();
                flying = wrapper.isFlying();
            });
        }
    }
}