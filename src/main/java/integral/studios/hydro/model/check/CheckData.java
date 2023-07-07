package integral.studios.hydro.model.check;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.type.*;
import integral.studios.hydro.service.CheckService;
import integral.studios.hydro.model.PlayerData;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CheckData {
    private Set<Check> checks;

    private Set<PacketCheck> packetChecks;

    private Set<PositionCheck> positionChecks;

    private Set<RotationCheck> rotationChecks;

    public void enable(PlayerData playerData) {
        CheckService checkService = Hydro.get(CheckService.class);

        checks = checkService.getConstructors().stream()
                .map(clazz -> {
                    try {
                        return (Check) clazz.newInstance(playerData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        packetChecks = checks.stream()
                .filter(PacketCheck.class::isInstance)
                .map(PacketCheck.class::cast)
                .collect(Collectors.toSet());

        positionChecks = checks.stream()
                .filter(PositionCheck.class::isInstance)
                .map(PositionCheck.class::cast)
                .collect(Collectors.toSet());

        rotationChecks = checks.stream()
                .filter(RotationCheck.class::isInstance)
                .map(RotationCheck.class::cast)
                .collect(Collectors.toSet());
    }
}