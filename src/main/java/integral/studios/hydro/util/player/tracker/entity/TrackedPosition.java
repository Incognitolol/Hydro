package integral.studios.hydro.util.player.tracker.entity;

import lombok.Getter;


@Getter
public class TrackedPosition {

    private static final double COORDINATE_SCALE = 32.0;
    private static final int DEFAULT_INTERPOLATION_TICKS = 3;

    // Interpolation thresholds (from Minecraft source)
    private static final double TELEPORT_THRESHOLD_X = 0.03125;  // 1/32
    private static final double TELEPORT_THRESHOLD_Y = 0.015625; // 1/64
    private static final double TELEPORT_THRESHOLD_Z = 0.03125;  // 1/32

    private int interpolationTicks;
    private double targetX, targetY, targetZ;

    private double posX, posY, posZ;

    private int serverPosX, serverPosY, serverPosZ;


    public TrackedPosition(double x, double y, double z) {
        this.serverPosX = encodeCoordinate(x);
        this.serverPosY = encodeCoordinate(y);
        this.serverPosZ = encodeCoordinate(z);

        this.posX = decodeCoordinate(serverPosX);
        this.posY = decodeCoordinate(serverPosY);
        this.posZ = decodeCoordinate(serverPosZ);

        this.targetX = this.posX;
        this.targetY = this.posY;
        this.targetZ = this.posZ;
    }


    public void onLivingUpdate() {
        if (interpolationTicks > 0) {
            // Interpolate towards target position
            double interpX = posX + (targetX - posX) / interpolationTicks;
            double interpY = posY + (targetY - posY) / interpolationTicks;
            double interpZ = posZ + (targetZ - posZ) / interpolationTicks;

            setPosition(interpX, interpY, interpZ);
            interpolationTicks--;
        }
    }


    public void handleMovement(double deltaX, double deltaY, double deltaZ) {
        // Update server position with encoded deltas
        serverPosX += encodeCoordinate(deltaX);
        serverPosY += encodeCoordinate(deltaY);
        serverPosZ += encodeCoordinate(deltaZ);

        // Set new interpolation target
        setInterpolationTarget(
                decodeCoordinate(serverPosX),
                decodeCoordinate(serverPosY),
                decodeCoordinate(serverPosZ)
        );
    }

    public void handleTeleport(double x, double y, double z) {
        // Update server position
        serverPosX = encodeCoordinate(x);
        serverPosY = encodeCoordinate(y);
        serverPosZ = encodeCoordinate(z);

        double newX = decodeCoordinate(serverPosX);
        double newY = decodeCoordinate(serverPosY);
        double newZ = decodeCoordinate(serverPosZ);

        // Check if the teleport is significant enough to warrant immediate update
        if (isSignificantTeleport(newX, newY, newZ)) {
            // Immediate teleport for large distances
            setPosition(newX, newY, newZ);
            setInterpolationTarget(newX, newY, newZ);
        } else {
            // Small adjustment - use interpolation
            setInterpolationTarget(newX, newY, newZ);
        }
    }


    private void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    private void setInterpolationTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.interpolationTicks = DEFAULT_INTERPOLATION_TICKS;
    }

    private boolean isSignificantTeleport(double newX, double newY, double newZ) {
        return Math.abs(posX - newX) >= TELEPORT_THRESHOLD_X ||
                Math.abs(posY - newY) >= TELEPORT_THRESHOLD_Y ||
                Math.abs(posZ - newZ) >= TELEPORT_THRESHOLD_Z;
    }

    private static int encodeCoordinate(double coordinate) {
        return (int) Math.round(coordinate * COORDINATE_SCALE);
    }


    private static double decodeCoordinate(int protocolCoordinate) {
        return protocolCoordinate / COORDINATE_SCALE;
    }

    public TrackedPosition copy() {
        TrackedPosition copy = new TrackedPosition(posX, posY, posZ);
        copy.interpolationTicks = this.interpolationTicks;
        copy.targetX = this.targetX;
        copy.targetY = this.targetY;
        copy.targetZ = this.targetZ;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TrackedPosition)) return false;

        TrackedPosition other = (TrackedPosition) obj;
        return serverPosX == other.serverPosX &&
                serverPosY == other.serverPosY &&
                serverPosZ == other.serverPosZ;
    }

    @Override
    public int hashCode() {
        int result = serverPosX;
        result = 31 * result + serverPosY;
        result = 31 * result + serverPosZ;
        return result;
    }

    @Override
    public String toString() {
        return String.format("TrackedPosition{x=%.3f, y=%.3f, z=%.3f, interp=%d}",
                posX, posY, posZ, interpolationTicks);
    }
}