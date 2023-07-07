package integral.studios.hydro.model.check.impl.combat.velocity;

import com.github.retrooper.packetevents.util.Vector3d;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.location.CustomLocation;

import java.util.List;
import java.util.stream.Collectors;

public class VelocityA extends PositionCheck {
    public VelocityA(PlayerData playerData) {
        super(playerData, "Velocity A", "Modification of Vertical Velocity", new ViolationHandler(25, 120L), Category.COMBAT, SubCategory.VELOCITY);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
        // if during the velocity, the vertical movement could possibly be altered, we exempt.
        if (!velocityTracker.isOnFirstTick()
                || collisionTracker.isUnderBlock()
                || collisionTracker.isOnClimbable()
                || collisionTracker.isInWater()
                || collisionTracker.isInLava()
                || collisionTracker.isInWeb()
                || movementTracker.getDeltaY() >= 0.42F) {
            decreaseVl(0.01D);
            return;
        }

        final double delta = movementTracker.getDeltaY();

        final List<Double> velocities = velocityTracker.getPossibleVelocities().stream().mapToDouble(Vector3d::getY).
                boxed().collect(Collectors.toList());

        double offset = velocities.stream().mapToDouble(velocity -> {
            double difference = Math.abs(delta - velocity);

            // account for possible 0.03
            if (difference > 1E-6D) {
                double fixed = (velocity - 0.08D) * 0.98F;

                if (Math.abs(fixed - delta) < difference) {
                    difference = Math.abs(fixed - delta);
                }
            }

            return difference;
        }).min().orElse(0.0D);

        if (delta > 0.0D && velocities.stream().anyMatch(velocity -> velocity >= 0.003D) && offset > 1E-10D) {
            if (++vl > 3D) {
                vl = 2D;

                handleViolation(new DetailedPlayerViolation(this,
                        "\n- §3Offset: §b" + offset
                                + "\n- §3First velocity §b" + velocities.get(0)
                                + "\n- §3Delta-Y: §b" + delta

                ));
            }
        } else {
            decreaseVl(0.1D);
        }
    }
}