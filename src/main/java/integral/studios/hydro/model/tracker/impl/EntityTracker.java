package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.player.tracker.entity.TrackedEntity;
import integral.studios.hydro.util.packet.PacketUtil;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
public class EntityTracker extends Tracker {
    // Thread-safe entity storage
    private final Map<Integer, TrackedEntity> entityMap = new ConcurrentHashMap<>();

    public EntityTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            handleSpawnLivingEntity(event);
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            handleSpawnPlayer(event);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            handleEntityRelativeMove(event);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            handleEntityRelativeMoveAndRotation(event);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            handleEntityTeleport(event);
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            handleDestroyEntities(event);
        }
    }

    @Override
    public void registerIncomingPostHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            handleFlyingPacket();
        }
    }

    private void handleSpawnLivingEntity(PacketSendEvent event) {
        WrapperPlayServerSpawnLivingEntity wrapper = new WrapperPlayServerSpawnLivingEntity(event);
        Vector3d position = wrapper.getPosition();
        int entityId = wrapper.getEntityId();

        TrackedEntity entity = new TrackedEntity(entityId, position.x, position.y, position.z);
        entityMap.put(entityId, entity);
    }

    private void handleSpawnPlayer(PacketSendEvent event) {
        WrapperPlayServerSpawnPlayer wrapper = new WrapperPlayServerSpawnPlayer(event);
        Vector3d position = wrapper.getPosition();
        int entityId = wrapper.getEntityId();

        TrackedEntity entity = new TrackedEntity(entityId, position.x, position.y, position.z);
        entityMap.put(entityId, entity);
    }

    private void handleEntityRelativeMove(PacketSendEvent event) {
        WrapperPlayServerEntityRelativeMove wrapper = new WrapperPlayServerEntityRelativeMove(event);

        TrackedEntity entity = entityMap.get(wrapper.getEntityId());
        if (entity == null) {
            return;
        }

        playerData.getTransactionTracker().confirmPre(() -> {
            entity.handleMovement(wrapper.getDeltaX(), wrapper.getDeltaY(), wrapper.getDeltaZ());
        });

        playerData.getTransactionTracker().confirmPost(this::updateAllEntitiesPostTransaction);
    }

    private void handleEntityRelativeMoveAndRotation(PacketSendEvent event) {
        WrapperPlayServerEntityRelativeMoveAndRotation wrapper = new WrapperPlayServerEntityRelativeMoveAndRotation(event);

        TrackedEntity entity = entityMap.get(wrapper.getEntityId());
        if (entity == null) {
            return;
        }

        playerData.getTransactionTracker().confirmPre(() -> {
            entity.handleMovement(wrapper.getDeltaX(), wrapper.getDeltaY(), wrapper.getDeltaZ());
        });

        playerData.getTransactionTracker().confirmPost(this::updateAllEntitiesPostTransaction);
    }

    private void handleEntityTeleport(PacketSendEvent event) {
        WrapperPlayServerEntityTeleport wrapper = new WrapperPlayServerEntityTeleport(event);

        TrackedEntity entity = entityMap.get(wrapper.getEntityId());
        if (entity == null) {
            return;
        }

        Vector3d position = wrapper.getPosition();

        playerData.getTransactionTracker().confirmPre(() -> {
            entity.handleTeleport(position.x, position.y, position.z);
        });

        playerData.getTransactionTracker().confirmPost(this::updateAllEntitiesPostTransaction);
    }

    private void handleDestroyEntities(PacketSendEvent event) {
        WrapperPlayServerDestroyEntities wrapper = new WrapperPlayServerDestroyEntities(event);

        for (int entityId : wrapper.getEntityIds()) {
            entityMap.remove(entityId);
        }
    }

    private void handleFlyingPacket() {
        entityMap.values().forEach(TrackedEntity::handlePostFlying);
    }

    private void updateAllEntitiesPostTransaction() {
        entityMap.values().forEach(TrackedEntity::handlePostTransaction);
    }

    public TrackedEntity get(int entityId) {
        return entityMap.get(entityId);
    }

    public boolean isTracking(int entityId) {
        return entityMap.containsKey(entityId);
    }

    public int getTrackedEntityCount() {
        return entityMap.size();
    }


    public TrackedEntity removeEntity(int entityId) {
        return entityMap.remove(entityId);
    }


    public void clearEntities() {
        entityMap.clear();
    }


    public double getDistanceToEntity(int entityId) {
        TrackedEntity entity = entityMap.get(entityId);
        if (entity == null) {
            return -1;
        }

        double dx = entity.getX() - playerData.getPositionTracker().getX();
        double dy = entity.getY() - playerData.getPositionTracker().getY();
        double dz = entity.getZ() - playerData.getPositionTracker().getZ();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public TrackedEntity getClosestEntity() {
        TrackedEntity closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<Integer, TrackedEntity> entry : entityMap.entrySet()) {
            TrackedEntity entity = entry.getValue();
            double distance = getDistanceToEntity(entry.getKey());

            if (distance >= 0 && distance < minDistance) {
                minDistance = distance;
                closest = entity;
            }
        }

        return closest;
    }


    public Map<Integer, TrackedEntity> getEntitiesWithinDistance(double maxDistance) {
        Map<Integer, TrackedEntity> nearbyEntities = new ConcurrentHashMap<>();

        for (Map.Entry<Integer, TrackedEntity> entry : entityMap.entrySet()) {
            double distance = getDistanceToEntity(entry.getKey());
            if (distance >= 0 && distance <= maxDistance) {
                nearbyEntities.put(entry.getKey(), entry.getValue());
            }
        }

        return nearbyEntities;
    }

    public int getEntityId(TrackedEntity entity) {
        if (entity == null) return -1;

        for (Map.Entry<Integer, TrackedEntity> entry : entityMap.entrySet()) {
            if (entry.getValue() == entity) {
                return entry.getKey();
            }
        }

        return -1;
    }
}