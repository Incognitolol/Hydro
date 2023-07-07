package integral.studios.hydro.model.check.impl.movement.speed;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.location.CustomLocation;

public class SpeedB extends PositionCheck {
    public SpeedB(PlayerData playerData) {
        super(playerData, "Movement Validation", "Movement Emulation Check", ViolationHandler.EXPERIMENTAL, Category.MOVEMENT, SubCategory.SPEED);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
        double deltaXZ = movementTracker.getDeltaXZ();

        double lowestMatch = movementEmulationTracker.getLowestMatch();
        double minMove = playerData.getActionTracker().isSprinting() ? 0.18 : (playerData.getActionTracker().isSneaking() ? 0.06 : 0.1475);

        if (deltaXZ > minMove && lowestMatch > 5E-4) {
            if (++vl > 5) {
                handleViolation(new DetailedPlayerViolation(this,
                        "\n- §3DeltaXZ: §b" + deltaXZ
                                + "\n- §3LowestMatch-Y: §b" + lowestMatch
                ));

                vl = 4;
            }
        } else {
            decreaseVl(0.01);
        }
    }
}