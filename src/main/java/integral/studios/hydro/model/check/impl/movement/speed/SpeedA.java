package integral.studios.hydro.model.check.impl.movement.speed;

import com.github.retrooper.packetevents.util.Vector3d;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.math.TagsBuilder;
import integral.studios.hydro.util.nms.NmsUtil;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class SpeedA extends PositionCheck {
    private double friction = 0.91F;

    private double lastDeltaXZ;

    public SpeedA(PlayerData playerData) {
        super(playerData, "Speed A", "Friction Check", "Mexify", new ViolationHandler(25, 300L), Category.MOVEMENT, SubCategory.SPEED);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
        EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData.getBukkitPlayer());

        TagsBuilder tags = new TagsBuilder();

        double deltaXZ = movementTracker.getDeltaXZ();
        double deltaY = movementTracker.getDeltaY();

        double movementSpeed = attributeTracker.getWalkSpeed();

        if (from.isOnGround()) {
            tags.addTag("Ground");

            // We apply math for sprinting which we assume is always true ong
            movementSpeed *= 1.3F;

            friction *= 0.91F;

            // Apply math for on ground
            movementSpeed *= 0.16277136F / (friction * friction * friction);

            if (!to.isOnGround() && (collisionTracker.isUnderBlock() || collisionTracker.isWasUnderBlock() || deltaY >= 0.42F)) {
                tags.addTag("Jump");

                // Compensate for jumpcuh
                movementSpeed += 0.2D;
            }
        } else {
            tags.addTag("Air");

            // Apply math for in aircuh
            movementSpeed = 0.026D;
            friction = 0.91F;
        }

        if (velocityTracker.isOnFirstTick()) {
            Vector3d velocity = velocityTracker.peekVelocity();

            tags.addTag("Velocity");

            // Compensate for velocity
            movementSpeed += MathUtil.hypot(velocity.getX(), velocity.getZ());
        }

        double ratio = (deltaXZ - lastDeltaXZ) / movementSpeed;

        if (ratio > 1.01D) {
            if (deltaXZ > 0.2) {
                if (++vl > 3) {
                    handleViolation(new DetailedPlayerViolation(this,
                            "\n- §3Ratio: §b" + ratio
                                    + "\n- §3DeltaXZ: §b" + deltaXZ
                                    + "\n- §3Delta-Y: §b" + deltaY
                                    + "\n- §3L-DeltaXZ: §b" + lastDeltaXZ
                                    + "\n- §3MovementSpeed: §b" + movementSpeed
                                    + "\n- §3Friction: §b" + friction
                                    + "\n- §3Scenarios: " + (tags.getSize() > 0 ? CC.GREEN : "") + tags.build()
                    ));
                }
            } else {
                decreaseVl(0.05);
            }
        } else {
            decreaseVl(0.05);
        }

        lastDeltaXZ = deltaXZ * friction;
        friction = NmsUtil.getBlockFriction(entityPlayer.world, to.getX(), to.getY() - 1, to.getZ());
    }
}