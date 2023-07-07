package integral.studios.hydro.model.check.violation.impl;

import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.violation.base.AbstractPlayerViolation;
import lombok.Getter;

@Getter
public class PlayerViolation extends AbstractPlayerViolation {
    private final int level;

    public PlayerViolation(Check check, int level) {
        super(check);

        this.level = level;
    }

    public PlayerViolation(Check check) {
        super(check);

        this.level = 1;
    }

}
