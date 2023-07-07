package integral.studios.hydro.model.check.violation.category;

import lombok.Getter;

public enum SubCategory {
    // Combat
    AIM_ASSIST(Category.COMBAT),
    AUTO_CLICKER(Category.COMBAT),
    KILL_AURA(Category.COMBAT),
    REACH(Category.COMBAT),
    VELOCITY(Category.COMBAT),

    // Misc
    BAD_PACKETS(Category.MISC),
    PING_SPOOF(Category.MISC),
    POST(Category.MISC),
    TIMER(Category.MISC),

    // Movement
    FLIGHT(Category.MOVEMENT),
    GROUND(Category.MISC),
    MOTION(Category.MOVEMENT),
    SPEED(Category.MOVEMENT
    );


    @Getter
    private final Category category;

    SubCategory(final Category category) {
        this.category = category;
    }
}