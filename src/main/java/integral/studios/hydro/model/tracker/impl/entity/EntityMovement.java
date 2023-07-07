package integral.studios.hydro.model.tracker.impl.entity;

import lombok.Data;

@Data
public class EntityMovement {
    private final double x, y, z;
    private final Type type;

    enum Type {
        RELATIVE,
        ABSOLUTE,
        NONE;
    }
}