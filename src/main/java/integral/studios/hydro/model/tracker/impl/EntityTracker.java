package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.tracker.impl.entity.TrackedEntity;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/*
 * Figure out what's wrong cuh
 * - Mexify
 */
@Getter
public class EntityTracker extends Tracker {
    private final Map<Integer, TrackedEntity> entityMap = new HashMap<>();

    public EntityTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            WrapperPlayServerSpawnLivingEntity wrapper = new WrapperPlayServerSpawnLivingEntity(event);

            Vector3d position = wrapper.getPosition();

            entityMap.putIfAbsent(wrapper.getEntityId(), new TrackedEntity(position.x, position.y, position.z));
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            WrapperPlayServerSpawnPlayer wrapper = new WrapperPlayServerSpawnPlayer(event);

            Vector3d position = wrapper.getPosition();

            entityMap.putIfAbsent(wrapper.getEntityId(), new TrackedEntity(position.x, position.y, position.z));
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove wrapper = new WrapperPlayServerEntityRelativeMove(event);

            if (!entityMap.containsKey(wrapper.getEntityId())) return;

            playerData.getTransactionTracker().confirmPre(() -> {
                get(wrapper.getEntityId()).handleMovement(wrapper.getDeltaX(), wrapper.getDeltaY(), wrapper.getDeltaZ());
            });

            playerData.getTransactionTracker().confirmPost(() -> entityMap.values().forEach(TrackedEntity::handlePostTransaction));
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation wrapper = new WrapperPlayServerEntityRelativeMoveAndRotation(event);

            if (!entityMap.containsKey(wrapper.getEntityId())) return;

            playerData.getTransactionTracker().confirmPre(() -> {
                get(wrapper.getEntityId()).handleMovement(wrapper.getDeltaX(), wrapper.getDeltaY(), wrapper.getDeltaZ());
            });

            playerData.getTransactionTracker().confirmPost(() -> entityMap.values().forEach(TrackedEntity::handlePostTransaction));
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport wrapper = new WrapperPlayServerEntityTeleport(event);

            if (!entityMap.containsKey(wrapper.getEntityId())) return;

            Vector3d position = wrapper.getPosition();

            playerData.getTransactionTracker().confirmPre(() -> {
                get(wrapper.getEntityId()).handleTeleport(position.x, position.y, position.z);
            });

            playerData.getTransactionTracker().confirmPost(() -> entityMap.values().forEach(TrackedEntity::handlePostTransaction));
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(event);

            int[] ids = destroyEntities.getEntityIds();

            for (int entityIds : ids) {
                entityMap.remove(entityIds);
            }
        }
    }

    @Override
    public void registerIncomingPostHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            entityMap.values().forEach(TrackedEntity::handlePostFlying);
        }
    }

    public TrackedEntity get(final int id) {
        return entityMap.get(id);
    }
}