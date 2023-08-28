package integral.studios.hydro.model.check.impl.combat.reach;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.tracker.impl.entity.TrackedEntity;
import integral.studios.hydro.model.tracker.impl.entity.TrackedPosition;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.math.client.ClientMath;
import integral.studios.hydro.util.math.client.impl.OptifineMath;
import integral.studios.hydro.util.math.client.impl.VanillaMath;
import integral.studios.hydro.util.mcp.*;
import integral.studios.hydro.util.packet.PacketHelper;

import java.util.concurrent.atomic.AtomicReference;

/*
 * This is purposefully broken before anyone complains figure it out.
 * It's not that hard if I can see what's wrong, and you can't just an IQ issue.
 * - Mexify
 */
public class Reach extends PacketCheck {
    private static final boolean[] BOOLEANS = {true, false};

    private static final double REACH_VALUE = 3.001D, HIT_BOX_VALUE = 10.0D;

    private final OptifineMath optifineMath = new OptifineMath();

    private final VanillaMath vanillaMath = new VanillaMath();

    private int entityId;

    private boolean attacked;

    private double hitBoxVL = -1;

    public Reach(PlayerData playerData) {
        super(playerData, "Reach", "Modification Of Attack Distance Check", new ViolationHandler(20, 160L), Category.COMBAT, SubCategory.REACH);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        // Handle incoming INTERACT_ENTITY packets
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interactEntity = new WrapperPlayClientInteractEntity(event);

            if (teleportTracker.isTeleporting()) {

                // Cancel the packet if the player is teleporting
                event.setCancelled(true);
                return;
            }

            if (interactEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                attacked = true;
                entityId = interactEntity.getEntityId();

                // Get the compensated entity from the entity tracker
                TrackedEntity compensatedEntity = entityTracker.getEntityMap().get(entityId);

                if (compensatedEntity == null) {

                    // Cancel the packet if the entity is not found
                    if (entityTracker.getEntityMap().containsKey(entityId)) {
                        event.setCancelled(true);
                    }
                    return;
                }

                if (attributeTracker.isCreativeMode()) {
                    // Cancel the packet if the player is in creative mode
                    return;
                }
            }
        }

        // Handle flying packets
        if (PacketHelper.isFlying(event.getPacketType())) {
            if (!attacked || teleportTracker.isTeleporting()) {

                // Skip the check if no attack happened or the player is teleporting
                return;
            }

            attacked = false;

            // Get the compensated entity from the entity tracker
            TrackedEntity compensatedEntity = entityTracker.get(entityId);

            if (compensatedEntity == null) {
                return;
            }

            AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);


            for (boolean fastMath : BOOLEANS) {
                ClientMath clientMath = fastMath ? optifineMath : vanillaMath;


                Vec3[] possibleEyeRotation = {

                        // Calculate the eye rotation based on different yaw and pitch values
                        MathUtil.getVectorForRotation(rotationTracker.getYaw(), rotationTracker.getPitch(), clientMath),
                        MathUtil.getVectorForRotation(rotationTracker.getLastYaw(), rotationTracker.getLastPitch(), clientMath),
                        MathUtil.getVectorForRotation(rotationTracker.getLastYaw(), rotationTracker.getPitch(), clientMath),
                };

                for (boolean sneaking : BOOLEANS) {
                    for (Vec3 eyeRotation : possibleEyeRotation) {
                        Vec3 eyePos = new Vec3(

                                // Calculate the eye position based on player's position and eye height
                                movementTracker.getLastX(),
                                movementTracker.getLastY() + MathUtil.getEyeHeight(sneaking),
                                movementTracker.getLastZ()
                        );

                        Vec3 endReachRay = eyePos.addVector(

                                // Calculate the end position of the reach ray
                                   eyeRotation.xCoord * 6.0D,
                                eyeRotation.yCoord * 6.0D,
                                eyeRotation.zCoord * 6.0D
                        );

                        for (TrackedPosition position : compensatedEntity.getPositions()) {
                            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                                    // Create an axis-aligned bounding box from entity's position
                                    position.getPosX(),
                                    position.getPosY(),
                                    position.getPosZ()
                            );

                            axisAlignedBB = axisAlignedBB.expand(0.1F, 0.1F, 0.1F);

                            if (playerData.getMovementTracker().getTicksSincePosition() > 0) {
                                axisAlignedBB = axisAlignedBB.expand(0.03, 0.03, 0.03);
                            }

                            // Calculate the intersection of the reach ray and the bounding box
                            MovingObjectPosition intercept = axisAlignedBB.calculateIntercept(eyePos, endReachRay);

                            if (intercept != null) {
                                double range = intercept.hitVec.distanceTo(eyePos);

                                if (range < minDistance.get()) {
                                    minDistance.set(range);
                                }
                            }
                        }
                    }
                }
            }

            if (minDistance.get() > HIT_BOX_VALUE) {
                if (++hitBoxVL > 40D) {
                    playerData.getCheckData().getChecks().stream().filter(check -> check.getName().equalsIgnoreCase("HitBox")).findAny().get()
                            .handleViolation(new DetailedPlayerViolation(this, "\n- §3HDistance: §b" + minDistance));
                }
            } else {
                hitBoxVL = Math.max(hitBoxVL - 5D, 0);
            }

            if (minDistance.get() > REACH_VALUE && minDistance.get() < HIT_BOX_VALUE) {
                if (++vl > 2.5D) {
                    handleViolation(new DetailedPlayerViolation(this, "\n- §3RDistance: §b" + minDistance));
                }
            } else if (minDistance.get() < REACH_VALUE) {
                decreaseVl(0.05D);
            }
        }
    }
}