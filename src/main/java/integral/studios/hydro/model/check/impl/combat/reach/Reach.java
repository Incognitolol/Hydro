package integral.studios.hydro.model.check.impl.combat.reach;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.mcp.AxisAlignedBB;
import integral.studios.hydro.util.mcp.MovingObjectPosition;
import integral.studios.hydro.util.mcp.Vec3;
import integral.studios.hydro.util.player.tracker.entity.TrackedEntity;
import integral.studios.hydro.util.player.tracker.entity.TrackedPosition;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.math.client.ClientMath;
import integral.studios.hydro.util.math.client.impl.OptifineMath;
import integral.studios.hydro.util.math.client.impl.VanillaMath;
import integral.studios.hydro.util.mcp.PlayerUtil;
import integral.studios.hydro.util.packet.PacketUtil;

public class Reach extends PacketCheck {

    // Constants
    private static final double SURVIVAL_REACH = 3.0;
    private static final double CREATIVE_REACH = 6.0;
    private static final double MAX_REACH_THRESHOLD = 0.001; // Small buffer for precision
    private static final double HITBOX_EXPANSION = 0.1;
    private static final double ZERO_THREE_EXPANSION = 0.03;
    private static final double RAY_LENGTH = 6.0;

    private static final double HITBOX_MAX_DISTANCE = 10.0;
    private static final int HITBOX_VL_THRESHOLD = 40;

    // Math implementations
    private final ClientMath[] mathImplementations = {
            new OptifineMath(),  // Fast math (Optifine)
            new VanillaMath()    // Standard math
    };

    private int pendingEntityId = -1;
    private boolean hasPendingAttack = false;

    private double hitboxViolations = 0;

    public Reach(PlayerData playerData) {
        super(playerData, "Reach", "Detects modified attack distance",
                new ViolationHandler(10, 160L), Category.COMBAT, SubCategory.REACH);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            handleInteractEntity(event);
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            handleFlyingPacket();
        }
    }

    private void handleInteractEntity(PacketReceiveEvent event) {
        WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);

        if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
            return;
        }

        if (teleportTracker.isTeleporting() && attributeTracker.getAbilities().isCreativeMode()) {
            event.setCancelled(true);
            return;
        }

        int entityId = wrapper.getEntityId();
        TrackedEntity entity = entityTracker.get(entityId);

        if (entity == null) {
            if (entityTracker.isTracking(entityId)) {
                event.setCancelled(true);
            }
            return;
        }

        if (attributeTracker.getAbilities().isCreativeMode()) {
            return;
        }

        pendingEntityId = entityId;
        hasPendingAttack = true;
    }

    private void handleFlyingPacket() {
        if (!hasPendingAttack || teleportTracker.isTeleporting()) {
            return;
        }

        hasPendingAttack = false;

        TrackedEntity entity = entityTracker.get(pendingEntityId);
        if (entity == null) {
            return;
        }

        double minDistance = calculateMinimumReachDistance(entity);

        if (minDistance > HITBOX_MAX_DISTANCE) {
            handleHitboxViolation(minDistance);
        } else {
            hitboxViolations = Math.max(0, hitboxViolations - 5);
        }

        double maxAllowedReach = getMaxAllowedReach();

        if (minDistance > maxAllowedReach && minDistance < HITBOX_MAX_DISTANCE) {
            if (++vl > 2.5) {
                handleViolation(new DetailedPlayerViolation(this,
                        String.format("%n- %sDistance: %s%.3f %s(max: %.3f)",
                                CC.PRI, CC.SEC, minDistance, CC.PRI, maxAllowedReach)));
            }
        } else if (minDistance <= maxAllowedReach) {
            decreaseVl(0.05);
        }
    }

    private double calculateMinimumReachDistance(TrackedEntity entity) {
        double minDistance = Double.MAX_VALUE;

        Vec3[] eyePositions = getPossibleEyePositions();
        Vec3[] eyeRotations = getPossibleEyeRotations();

        for (Vec3 eyePos : eyePositions) {
            for (Vec3 eyeRotation : eyeRotations) {

                Vec3 rayEnd = eyePos.addVector(
                        eyeRotation.xCoord * RAY_LENGTH,
                        eyeRotation.yCoord * RAY_LENGTH,
                        eyeRotation.zCoord * RAY_LENGTH
                );

                double distance = getMinDistanceToEntity(entity, eyePos, rayEnd);
                minDistance = Math.min(minDistance, distance);
            }
        }

        return minDistance;
    }

    private Vec3[] getPossibleEyePositions() {
        double normalEyeHeight = MathUtil.getEyeHeight(false);
        double sneakingEyeHeight = MathUtil.getEyeHeight(true);

        return new Vec3[] {
                new Vec3(positionTracker.getLastX(),
                        positionTracker.getLastY() + normalEyeHeight,
                        positionTracker.getLastZ()),

                new Vec3(positionTracker.getLastX(),
                        positionTracker.getLastY() + sneakingEyeHeight,
                        positionTracker.getLastZ())
        };
    }

    private Vec3[] getPossibleEyeRotations() {
        Vec3[] rotations = new Vec3[mathImplementations.length * 3];
        int index = 0;

        // Test with different math implementations
        for (ClientMath math : mathImplementations) {

            rotations[index++] = MathUtil.getVectorForRotation(
                    rotationTracker.getYaw(),
                    rotationTracker.getPitch(),
                    math
            );

            rotations[index++] = MathUtil.getVectorForRotation(
                    rotationTracker.getLastYaw(),
                    rotationTracker.getLastPitch(),
                    math
            );

            rotations[index++] = MathUtil.getVectorForRotation(
                    rotationTracker.getLastYaw(),
                    rotationTracker.getPitch(),
                    math
            );
        }

        return rotations;
    }

    private double getMinDistanceToEntity(TrackedEntity entity, Vec3 eyePos, Vec3 rayEnd) {
        double minDistance = Double.MAX_VALUE;

        minDistance = Math.min(minDistance,
                checkPositionDistance(entity.getConfirmedPosition(), eyePos, rayEnd));

        if (entity.hasPendingMovement()) {
            minDistance = Math.min(minDistance,
                    checkPositionDistance(entity.getPendingPosition(), eyePos, rayEnd));
        }

        for (TrackedPosition position : entity.getPositions()) {
            minDistance = Math.min(minDistance,
                    checkPositionDistance(position, eyePos, rayEnd));
        }

        return minDistance;
    }

    private double checkPositionDistance(TrackedPosition position, Vec3 eyePos, Vec3 rayEnd) {
        if (position == null) {
            return Double.MAX_VALUE;
        }

        // Get entity bounding box
        AxisAlignedBB boundingBox = PlayerUtil.getBoundingBox(
                position.getPosX(),
                position.getPosY(),
                position.getPosZ()
        );

        // Expand for hitbox
        boundingBox = boundingBox.expand(HITBOX_EXPANSION, HITBOX_EXPANSION, HITBOX_EXPANSION);

        // Additional expansion for 0.03 movement
        if (positionTracker.isPossiblyZeroThree()) {
            boundingBox = boundingBox.expand(ZERO_THREE_EXPANSION, ZERO_THREE_EXPANSION, ZERO_THREE_EXPANSION);
        }

        // Calculate ray intersection
        MovingObjectPosition intercept = boundingBox.calculateIntercept(eyePos, rayEnd);

        if (intercept != null && intercept.hitVec != null) {
            return intercept.hitVec.distanceTo(eyePos);
        }

        return Double.MAX_VALUE;
    }

    private double getMaxAllowedReach() {
        boolean isCreative = attributeTracker.getAbilities().isCreativeMode();
        return (isCreative ? CREATIVE_REACH : SURVIVAL_REACH) + MAX_REACH_THRESHOLD;
    }

    private void handleHitboxViolation(double distance) {
        hitboxViolations++;

        if (hitboxViolations > HITBOX_VL_THRESHOLD) {
            playerData.getCheckData().getChecks().stream()
                    .filter(check -> check.getName().equalsIgnoreCase("HitBox"))
                    .findFirst()
                    .ifPresent(hitboxCheck ->
                            hitboxCheck.handleViolation(new DetailedPlayerViolation(this,
                                    String.format("%n- %sHitbox Distance: %s%.3f", CC.PRI, CC.SEC, distance)))
                    );
        }
    }
}