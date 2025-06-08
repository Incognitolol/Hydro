package integral.studios.hydro.util.player.tracker.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class EntityMovement {
    private final double x;
    private final double y;
    private final double z;
    private final Type type;


    public enum Type {
        RELATIVE,
        ABSOLUTE,
        NONE
    }


    public static EntityMovement relative(double deltaX, double deltaY, double deltaZ) {
        return new EntityMovement(deltaX, deltaY, deltaZ, Type.RELATIVE);
    }

    public static EntityMovement absolute(double x, double y, double z) {
        return new EntityMovement(x, y, z, Type.ABSOLUTE);
    }

    public static EntityMovement none() {
        return new EntityMovement(0, 0, 0, Type.NONE);
    }

    public boolean hasMovement() {
        if (type == Type.NONE) return false;
        if (type == Type.RELATIVE) return x != 0 || y != 0 || z != 0;
        return true; // Absolute movement always counts as movement
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public boolean exceedsThreshold(double threshold) {
        return getMagnitude() > threshold;
    }

    @Override
    public String toString() {
        return String.format("EntityMovement{type=%s, x=%.3f, y=%.3f, z=%.3f}",
                type, x, y, z);
    }
}