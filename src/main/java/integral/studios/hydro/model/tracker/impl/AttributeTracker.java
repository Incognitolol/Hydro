package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.lag.ConfirmedAbilities;
import lombok.Getter;


@Getter
public class AttributeTracker extends Tracker {

    private static final double DEFAULT_WALK_SPEED = 0.1;
    private static final double SPRINT_MODIFIER = 0.3;
    private static final double SPEED_EFFECT_MODIFIER = 0.2;
    private static final double SLOWNESS_EFFECT_MODIFIER = -0.15;

    private static final int SPEED_EFFECT_ID = 1;
    private static final int SLOWNESS_EFFECT_ID = 2;
    private static final int JUMP_BOOST_EFFECT_ID = 8;

    private double walkSpeed = DEFAULT_WALK_SPEED;
    private int jumpBoost = 0;
    private int speedBoost = 0;
    private int slowness = 0;

    private ConfirmedAbilities abilities = new ConfirmedAbilities();
    private final ConfirmedAbilities lastAbilities = new ConfirmedAbilities();

    public AttributeTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            handlePlayerAbilities(event);
        } else if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            handleAttributeUpdate(event);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            handleEntityEffect(event);
        } else if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            handleRemoveEntityEffect(event);
        }
    }

    private void handlePlayerAbilities(PacketSendEvent event) {
        final WrapperPlayServerPlayerAbilities wrapper = new WrapperPlayServerPlayerAbilities(event);

        playerData.getTransactionTracker().confirmPre(() -> {
            abilities = new ConfirmedAbilities(
                    wrapper.isInGodMode(),
                    wrapper.isFlying(),
                    wrapper.isFlightAllowed(),
                    wrapper.isInCreativeMode(),
                    wrapper.getFOVModifier(),
                    wrapper.getFlySpeed()
            );
        });
    }

    private void handleAttributeUpdate(PacketSendEvent event) {
        WrapperPlayServerUpdateAttributes updateAttributes = new WrapperPlayServerUpdateAttributes(event);

        for (WrapperPlayServerUpdateAttributes.Property property : updateAttributes.getProperties()) {
            if ("generic.movementSpeed".equals(property.getKey())) {
                playerData.getTransactionTracker().confirmPre(() -> {
                    walkSpeed = property.getValue();
                });
                break;
            }
        }
    }

    private void handleEntityEffect(PacketSendEvent event) {
        WrapperPlayServerEntityEffect entityEffect = new WrapperPlayServerEntityEffect(event);

        if (entityEffect.getEntityId() != playerData.getEntityId()) {
            return;
        }

        int amplifier = entityEffect.getEffectAmplifier();
        int effectId = entityEffect.getPotionType().getId(playerData.getClientVersion());

        playerData.getTransactionTracker().confirmPre(() -> {
            applyEffect(effectId, amplifier + 1);
        });
    }

    private void handleRemoveEntityEffect(PacketSendEvent event) {
        WrapperPlayServerRemoveEntityEffect removeEntityEffect = new WrapperPlayServerRemoveEntityEffect(event);

        if (removeEntityEffect.getEntityId() != playerData.getEntityId()) {
            return;
        }

        int effectId = removeEntityEffect.getPotionType().getId(playerData.getClientVersion());

        playerData.getTransactionTracker().confirmPre(() -> {
            removeEffect(effectId);
        });
    }

    private void applyEffect(int effectId, int level) {
        switch (effectId) {
            case SPEED_EFFECT_ID:
                speedBoost = level;
                break;
            case SLOWNESS_EFFECT_ID:
                slowness = level;
                break;
            case JUMP_BOOST_EFFECT_ID:
                jumpBoost = level;
                break;
        }
    }

    private void removeEffect(int effectId) {
        switch (effectId) {
            case SPEED_EFFECT_ID:
                speedBoost = 0;
                break;
            case SLOWNESS_EFFECT_ID:
                slowness = 0;
                break;
            case JUMP_BOOST_EFFECT_ID:
                jumpBoost = 0;
                break;
        }
    }

    public double getMoveSpeed(boolean sprint) {
        double baseSpeed = walkSpeed;

        // Apply sprint modifier
        if (sprint) {
            baseSpeed *= (1.0 + SPRINT_MODIFIER);
        }

        // Apply potion effects
        if (speedBoost > 0) {
            baseSpeed *= (1.0 + (speedBoost * SPEED_EFFECT_MODIFIER));
        }

        if (slowness > 0) {
            baseSpeed *= (1.0 + (slowness * SLOWNESS_EFFECT_MODIFIER));
        }

        return baseSpeed;
    }

    public double getJumpVelocityModifier() {
        return jumpBoost * 0.1;
    }


    public void reset() {
        walkSpeed = DEFAULT_WALK_SPEED;
        jumpBoost = 0;
        speedBoost = 0;
        slowness = 0;
        abilities = new ConfirmedAbilities();
    }
}