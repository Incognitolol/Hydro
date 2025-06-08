package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.util.mcp.AxisAlignedBB;
import integral.studios.hydro.util.mcp.PlayerUtil;
import integral.studios.hydro.util.mcp.Vec3;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.math.client.ClientMath;
import integral.studios.hydro.util.math.client.impl.OptifineMath;
import integral.studios.hydro.util.math.client.impl.VanillaMath;
import integral.studios.hydro.util.mcp.MovingObjectPosition;
import integral.studios.hydro.util.packet.PacketUtil;
import integral.studios.hydro.util.player.tracker.entity.TrackedEntity;
import integral.studios.hydro.util.player.tracker.entity.TrackedPosition;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Getter
public class RotationTracker extends Tracker {
    private static final boolean[] BOOLEANS = {false, true};

    private static final OptifineMath optifineMath = new OptifineMath();

    private static final VanillaMath vanillaMath = new VanillaMath();

    private final Set<Integer> candidates = new HashSet<>();

    private float yaw, pitch, lastYaw, lastPitch, deltaYaw,
            deltaPitch, lastDeltaYaw, lastDeltaPitch, joltYaw, joltPitch,
            lastJoltYaw, lastJoltPitch, yawAccel, pitchAccel,
            lastYawAccel, lastPitchAccel, divisorX, divisorY;

    private float gcdYaw, gcdPitch, absGcdPitch, absGcdYaw;

    private int sensitivity, calcSensitivity;

    private int ticksSinceRotation;

    private double angle, lastAngle, lastLastAgnle;

    public RotationTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            lastYaw = yaw;
            lastPitch = pitch;

            lastJoltPitch = joltPitch;
            lastJoltYaw = joltYaw;

            lastDeltaYaw = deltaYaw;
            lastDeltaPitch = deltaPitch;

            lastYawAccel = yawAccel;
            lastPitchAccel = pitchAccel;

            lastLastAgnle = lastAngle;
            lastAngle = angle;

            if (playerFlying.hasRotationChanged()) {
                yaw = playerFlying.getLocation().getYaw();
                pitch = playerFlying.getLocation().getPitch();

                yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
                pitchAccel = Math.abs(deltaPitch - lastDeltaPitch);

                deltaYaw = Math.abs(yaw - lastYaw);
                deltaPitch = Math.abs(pitch - lastPitch);

                joltYaw = Math.abs(deltaYaw - lastDeltaYaw);
                joltPitch = Math.abs(deltaPitch - lastDeltaPitch);

                long expandedYaw = (long) (deltaYaw * MathUtil.EXPANDER);
                long previousExpandedYaw = (long) (lastDeltaYaw * MathUtil.EXPANDER);

                gcdYaw = (float) (MathUtil.getGcd(expandedYaw, previousExpandedYaw) / MathUtil.EXPANDER);

                long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                long previousExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                gcdPitch = (float) (MathUtil.getGcd(expandedPitch, previousExpandedPitch) / MathUtil.EXPANDER);

                absGcdYaw = (float) (MathUtil.getGcd(Math.abs(expandedYaw), Math.abs(previousExpandedYaw)) / MathUtil.EXPANDER);
                absGcdPitch = (float) (MathUtil.getGcd(Math.abs(expandedPitch), Math.abs(previousExpandedPitch)) / MathUtil.EXPANDER);

                divisorX = MathUtil.getRotationGcd(deltaPitch, lastDeltaPitch);
                divisorY = MathUtil.getRotationGcd(deltaYaw, lastDeltaYaw);

                processSensitivity();

                ticksSinceRotation = 0;

                if (playerData.getActionTracker().getTicksSinceAttack() < 20) {
                    TrackedEntity entity = playerData.getEntityTracker().get(playerData.getActionTracker().getLastAttackedEntityId());

                    if (entity == null) {
                        angle = Double.NaN;
                        return;
                    }

                    AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);

                    Vec3 hitVec = null, center = null;

                    for (boolean fastMath : BOOLEANS) {
                        ClientMath clientMath = fastMath ? optifineMath : vanillaMath;

                        Vec3[] possibleEyeRotation = {
                                MathUtil.getVectorForRotation(playerData.getRotationTracker().getYaw(), playerData.getRotationTracker().getPitch(), clientMath),
                                MathUtil.getVectorForRotation(playerData.getRotationTracker().getLastYaw(), playerData.getRotationTracker().getLastPitch(), clientMath),
                                MathUtil.getVectorForRotation(playerData.getRotationTracker().getLastYaw(), playerData.getRotationTracker().getPitch(), clientMath),
                        };

                        for (boolean sneaking : BOOLEANS) {
                            for (Vec3 eyeRotation : possibleEyeRotation) {
                                Vec3 eyePos = new Vec3(
                                        playerData.getPositionTracker().getLastX(),
                                        playerData.getPositionTracker().getLastY() + MathUtil.getEyeHeight(sneaking),
                                        playerData.getPositionTracker().getLastZ()
                                );

                                Vec3 endReachRay = eyePos.addVector(
                                        eyeRotation.xCoord * 6.0D,
                                        eyeRotation.yCoord * 6.0D,
                                        eyeRotation.zCoord * 6.0D
                                );

                                for (TrackedPosition position : entity.getPositions()) {
                                    AxisAlignedBB axisAlignedBB = PlayerUtil.getBoundingBox(
                                            position.getPosX(),
                                            position.getPosY(),
                                            position.getPosZ()
                                    );

                                    axisAlignedBB = axisAlignedBB.expand(0.1F, 0.1F, 0.1F);

                                    if (playerData.getPositionTracker().isPossiblyZeroThree()) {
                                        axisAlignedBB = axisAlignedBB.expand(0.03, 0.03, 0.03);
                                    }

                                    MovingObjectPosition intercept = axisAlignedBB.calculateIntercept(eyePos, endReachRay);

                                    if (intercept != null) {
                                        double range = intercept.hitVec.distanceTo(eyePos);

                                        if (range < minDistance.get()) {
                                            minDistance.set(range);

                                            center = axisAlignedBB.getCenter();
                                            hitVec = intercept.hitVec;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (minDistance.get() == Double.MAX_VALUE || center == null || hitVec == null)
                        return;

                    Vec3 eyePos = new Vec3(
                            playerData.getPositionTracker().getLastX(),
                            playerData.getPositionTracker().getLastY() + MathUtil.getEyeHeight(false),
                            playerData.getPositionTracker().getLastZ()
                    );

                    Vector boundingBoxDiffXZ = new Vector(eyePos.xCoord - center.xCoord, 0, eyePos.zCoord - center.zCoord);
                    Vector hitVecDiffXZ = new Vector(eyePos.xCoord - hitVec.xCoord, 0, eyePos.zCoord - hitVec.zCoord);

                    angle = hitVecDiffXZ.angle(boundingBoxDiffXZ);
                }
            } else {
                ++ticksSinceRotation;
            }
        }
    }

    public void processSensitivity() {
        float pitch = getPitch();
        float lastPitch = getLastPitch();

        float yaw = getYaw();
        float lastYaw = getLastYaw();

        if (Math.abs(pitch) != 90.0f) {
            float distanceY = pitch - lastPitch;

            double error = Math.max(Math.abs(pitch), Math.abs(lastPitch)) * 3.814697265625E-6;

            computeSensitivity(distanceY, error);
        }

        float distanceX = circularDistance(yaw, lastYaw);

        double error = Math.max(Math.abs(yaw), Math.abs(lastYaw)) * 3.814697265625E-6;

        computeSensitivity(distanceX, error);

        if (candidates.size() == 1) {
            calcSensitivity = candidates.iterator().next();
            sensitivity = 200 * calcSensitivity / 143;
        } else {
            sensitivity = -1;

            forEach(candidates::add);
        }
    }

    public void computeSensitivity(double delta, double error) {
        double start = delta - error;
        double end = delta + error;

        forEach(s -> {
            double f0 = ((double) s / 142.0) * 0.6 + 0.2;
            double f = (f0 * f0 * f0 * 8.0) * 0.15;

            int pStart = (int) Math.ceil(start / f);
            int pEnd = (int) Math.floor(end / f);

            if (pStart <= pEnd) {
                for (int p = pStart; p <= pEnd; p++) {
                    double d = p * f;

                    if (!(d >= start && d <= end)) {
                        candidates.remove(s);
                    }
                }
            } else {
                candidates.remove(s);
            }
        });
    }

    public float circularDistance(float a, float b) {
        float d = Math.abs(a % 360.0f - b % 360.0f);
        return d < 180.0f ? d : 360.0f - d;
    }

    public void forEach(Consumer<Integer> consumer) {
        for (int s = 0; s <= 143; s++) {
            consumer.accept(s);
        }
    }
}