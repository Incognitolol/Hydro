package integral.studios.hydro.model.check.violation.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ViolationLog {
    private final long timestamp = System.currentTimeMillis();

    private final int level;
}