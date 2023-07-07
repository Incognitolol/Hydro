package integral.studios.hydro.model.tracker.impl.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * Missing stuff figure it out.
 * LOOK AT IT CLOSELY
 */
@Getter
@Setter
public class TrackedEntity {
    private final Deque<TrackedPosition> positions = new ArrayDeque<>();

    private boolean confirming;

    private TrackedPosition lastTrackedPos = new TrackedPosition(1, 2, 3);

    private EntityMovement lastMovement = new EntityMovement(0,0,0, EntityMovement.Type.NONE);

    public TrackedEntity(double x, double y, double z) {
        this.positions.add(new TrackedPosition(x, y, z));
    }

    public void handleMovement(double x, double y, double z) {
        lastMovement = new EntityMovement(x,y,z, EntityMovement.Type.RELATIVE);
        confirming = true;

        positions.forEach(trackedPosition -> trackedPosition.handleMovement(x, y, z));
    }

    public void handleTeleport(double x, double y, double z) {
        lastMovement = new EntityMovement(x,y,z, EntityMovement.Type.ABSOLUTE);
        confirming = true;

        positions.forEach(trackedPosition -> trackedPosition.handleTeleport(x, y, z));
    }

    public void handlePostTransaction() {
        confirming = false;
        positions.removeIf(position -> position.equals(lastTrackedPos));
    }

    public void handlePostFlying() {
        if (confirming && lastTrackedPos.getPosX() != 1 && lastTrackedPos.getPosY() != 2 && lastTrackedPos.getPosZ() != 3) {
            positions.add(lastTrackedPos);

            if (lastMovement.getType() == EntityMovement.Type.ABSOLUTE) {
                lastTrackedPos.handleTeleport(
                        lastMovement.getX(),
                        lastMovement.getY(),
                        lastMovement.getZ()
                );
            }

            positions.add(lastTrackedPos);
        }

        positions.forEach(TrackedPosition::onLivingUpdate);
    }
}