package integral.studios.hydro.model.check.violation.punishment;

import integral.studios.hydro.model.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlayerBan {
    private final PlayerData playerData;

    private final String reason;
}