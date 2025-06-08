package integral.studios.hydro.util.player.tracker.entity;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;


@Getter
public class TrackedEntity {
    private final Deque<TrackedPosition> positions = new ArrayDeque<>();
    private TrackedPosition confirmedPosition;
    private TrackedPosition pendingPosition;

    private EntityMovement pendingMovement = EntityMovement.none();
    private boolean awaitingConfirmation = false;

    private final int entityId;

    public TrackedEntity(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        TrackedPosition initialPos = new TrackedPosition(x, y, z);
        this.positions.add(initialPos);
        this.confirmedPosition = initialPos;
        this.pendingPosition = initialPos.copy();
    }

    public TrackedEntity(double x, double y, double z) {
        this(-1, x, y, z);
    }


    public void handleMovement(double deltaX, double deltaY, double deltaZ) {
        pendingMovement = EntityMovement.relative(deltaX, deltaY, deltaZ);
        awaitingConfirmation = true;

        // Apply movement to all tracked positions
        positions.forEach(pos -> pos.handleMovement(deltaX, deltaY, deltaZ));
    }

    public void handleTeleport(double x, double y, double z) {
        pendingMovement = EntityMovement.absolute(x, y, z);
        awaitingConfirmation = true;

        // Apply teleport to all tracked positions
        positions.forEach(pos -> pos.handleTeleport(x, y, z));
    }

    public void handlePostTransaction() {
        if (!awaitingConfirmation) {
            return;
        }

        awaitingConfirmation = false;

        // Clean up old positions that match the confirmed position
        positions.removeIf(pos -> pos.equals(confirmedPosition));

        // Update confirmed position if we have new positions
        if (!positions.isEmpty()) {
            confirmedPosition = positions.poll();
            // Keep at least one position in the queue
            if (positions.isEmpty()) {
                positions.add(confirmedPosition.copy());
            }
        }
    }


    public void handlePostFlying() {
        // Apply pending movement if we're waiting for confirmation
        if (awaitingConfirmation && pendingMovement.hasMovement()) {
            // Create new position state for the pending movement
            TrackedPosition newPosition = confirmedPosition.copy();

            // Apply the movement based on type
            if (pendingMovement.getType() == EntityMovement.Type.ABSOLUTE) {
                newPosition.handleTeleport(
                        pendingMovement.getX(),
                        pendingMovement.getY(),
                        pendingMovement.getZ()
                );
            } else if (pendingMovement.getType() == EntityMovement.Type.RELATIVE) {
                newPosition.handleMovement(
                        pendingMovement.getX(),
                        pendingMovement.getY(),
                        pendingMovement.getZ()
                );
            }

            // Add to tracking queue if not already present
            if (!positions.contains(newPosition)) {
                positions.add(newPosition);
            }

            pendingPosition = newPosition;
        }

        // Update all positions (interpolation)
        positions.forEach(TrackedPosition::onLivingUpdate);
        confirmedPosition.onLivingUpdate();
    }


    public double getX() {
        return confirmedPosition.getPosX();
    }


    public double getY() {
        return confirmedPosition.getPosY();
    }

    public double getZ() {
        return confirmedPosition.getPosZ();
    }

    public TrackedPosition getPendingPosition() {
        return awaitingConfirmation ? pendingPosition : confirmedPosition;
    }

    public int getPositionQueueSize() {
        return positions.size();
    }


    public boolean hasPendingMovement() {
        return awaitingConfirmation;
    }

    public double getPendingDistance() {
        if (!awaitingConfirmation) return 0;

        double dx = pendingPosition.getPosX() - confirmedPosition.getPosX();
        double dy = pendingPosition.getPosY() - confirmedPosition.getPosY();
        double dz = pendingPosition.getPosZ() - confirmedPosition.getPosZ();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void reset(double x, double y, double z) {
        positions.clear();
        TrackedPosition newPos = new TrackedPosition(x, y, z);
        positions.add(newPos);
        confirmedPosition = newPos;
        pendingPosition = newPos.copy();
        pendingMovement = EntityMovement.none();
        awaitingConfirmation = false;
    }

    @Override
    public String toString() {
        return String.format("TrackedEntity{id=%d, pos=(%.2f,%.2f,%.2f), pending=%s, queueSize=%d}",
                entityId, getX(), getY(), getZ(), awaitingConfirmation, positions.size());
    }
}