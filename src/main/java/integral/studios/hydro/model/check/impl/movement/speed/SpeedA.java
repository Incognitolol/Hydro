package integral.studios.hydro.model.check.impl.movement.speed;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.tag.TagsBuilder;
import org.bukkit.Bukkit;

public class SpeedA extends PositionCheck {
    private double lastDeltaXZ;

    public SpeedA(PlayerData playerData) {
        super(playerData, "Speed A", "Friction Check", new ViolationHandler(25, 300L), Category.MOVEMENT, SubCategory.SPEED);
    }

    @Override
    public void handle() {
        if (attributeTracker.getAbilities().isFlying()
                || attributeTracker.getAbilities().isFlightAllowed()
                || attributeTracker.getAbilities().isCreativeMode()) {
            return;
        }

        TagsBuilder tags = new TagsBuilder();

        double deltaXZ = positionTracker.getDeltaXZ();
        double deltaY = positionTracker.getDeltaY();

        if (velocityTracker.isOnFirstTick()) {
            // Do this so on split if they get a slower velocity they wont false
            // Theres not really any advantage that can be given here either since we can check
            // In a velocity check
            double velocity = Math.max(
                    lastDeltaXZ,
                    velocityTracker.getPossibleVelocities().stream().mapToDouble(vector
                            -> Math.hypot(vector.x, vector.z)).max().getAsDouble()
            );

            // Compensate for player velocity
            lastDeltaXZ = velocity;
        }

        // Max offset limit
        double baseMovementSpeed = attributeTracker.getWalkSpeed();

        // We apply math for sprinting which we assume is always true
        baseMovementSpeed *= 1.3F;

        int speed = attributeTracker.getSpeedBoost();
        int slow = attributeTracker.getSlowness();

        baseMovementSpeed += baseMovementSpeed * speed * 0.2F;
        baseMovementSpeed += baseMovementSpeed * slow * -0.15F;

        float movementSpeed = (float) baseMovementSpeed;

        float friction = 0.91F;

        if (collisionTracker.isLastClientGround()) {
            friction *= playerData.getCollisionTracker().getLastFriction();
        }

        if (collisionTracker.isLastClientGround()) {
            tags.addTag("Last Ground");

            // Apply math for on ground
            movementSpeed *= 0.16277136F / (friction * friction * friction);
        } else {
            tags.addTag("Last Air");

            // Apply math for in air
            movementSpeed = (float) ((double) 0.02F + (double) 0.02F * 0.3D);
        }

        boolean jumpPossible = !collisionTracker.isClientGround()
                && collisionTracker.isLastClientGround()
                && (collisionTracker.isBonking()
                || collisionTracker.isLastBonking()
                || deltaY >= 0.42F);

        if (jumpPossible) {
            tags.addTag("Jump");

            // Compensate for player jump
            lastDeltaXZ += 0.2D;
        }

        lastDeltaXZ += movementSpeed;

        double offset = deltaXZ - lastDeltaXZ;

        if (positionTracker.isPossiblyZeroThree()) {
            tags.addTag("Desync");

            offset -= 0.03;
        }

        if (offset > 0.001) {
            if (deltaXZ > 0.2) {
                if (++vl > 2) {
                    handleViolation(new DetailedPlayerViolation(this,
                            "\n- " + CC.PRI + "Offset: " + CC.SEC + offset
                                    + "\n- " + CC.PRI + "DeltaXZ: " + CC.SEC + deltaXZ
                                    + "\n- " + CC.PRI + "Delta-Y: " + CC.SEC + deltaY
                                    + "\n- " + CC.PRI + "Predicted: " + CC.SEC + lastDeltaXZ
                                    + "\n- " + CC.PRI + "MovementSpeed: " + CC.SEC + movementSpeed
                                    + "\n- " + CC.PRI + "Friction: " + CC.SEC + friction
                                    + "\n- " + CC.PRI + "Ground: " + CC.SEC + collisionTracker.isClientGround()
                                    + "\n- " + CC.PRI + "Scenarios: " + (tags.getSize() > 0 ? CC.GREEN : "") + tags.build()
                    ));
                }
            } else {
                decreaseVl(0.05);
            }
        } else {
            decreaseVl(0.05);
        }

        lastDeltaXZ = deltaXZ * friction;
    }
}