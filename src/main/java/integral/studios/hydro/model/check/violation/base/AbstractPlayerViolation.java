package integral.studios.hydro.model.check.violation.base;

import integral.studios.hydro.model.check.Check;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractPlayerViolation implements Violation {
    private final Check check;
}
