package integral.studios.hydro.util.lag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class ConfirmedAbilities {
    private boolean godMode, flying, flightAllowed, creativeMode;

    private float walkSpeed, flySpeed;

    public ConfirmedAbilities() {
        this(
                false,
                false,
                false,
                false,
                0.1F,
                0.05F
        );
    }

    public ConfirmedAbilities clone() {
        return new ConfirmedAbilities(godMode, flying, flightAllowed, creativeMode, walkSpeed, flySpeed);
    }
}
