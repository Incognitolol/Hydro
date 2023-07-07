package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class RotationTracker extends Tracker {
    private final Set<Integer> candidates = new HashSet<>();

    private float yaw, pitch, lastYaw, lastPitch, deltaYaw,
            deltaPitch, lastDeltaYaw, lastDeltaPitch, joltYaw, joltPitch,
            lastJoltYaw, lastJoltPitch, yawAccel, pitchAccel,
            lastYawAccel, lastPitchAccel;

    private double gcdYaw, gcdPitch, absGcdPitch, absGcdYaw;

    private int sensitivity, calcSensitivity;

    public RotationTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            if (PacketHelper.isRotation(event.getPacketType())) {
                lastYaw = yaw;
                lastPitch = pitch;

                lastJoltPitch = joltPitch;
                lastJoltYaw = joltYaw;

                yaw = playerFlying.getLocation().getYaw();
                pitch = playerFlying.getLocation().getPitch();

                lastDeltaYaw = deltaYaw;
                lastDeltaPitch = deltaPitch;

                lastYawAccel = yawAccel;
                lastPitchAccel = pitchAccel;

                yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
                pitchAccel = Math.abs(deltaPitch - lastDeltaPitch);

                deltaYaw = Math.abs(yaw - lastYaw);
                deltaPitch = Math.abs(pitch - lastPitch);

                joltYaw = Math.abs(deltaYaw - lastDeltaYaw);
                joltPitch = Math.abs(deltaPitch - lastDeltaPitch);

                gcdYaw = MathUtil.getGcd(deltaYaw, lastDeltaYaw);
                gcdPitch = MathUtil.getGcd(deltaPitch, lastDeltaPitch);

                absGcdYaw = MathUtil.getGcd(Math.abs(deltaYaw), Math.abs(lastDeltaYaw));
                absGcdPitch = MathUtil.getGcd(Math.abs(deltaPitch), Math.abs(lastDeltaPitch));

                processSensitivity();

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