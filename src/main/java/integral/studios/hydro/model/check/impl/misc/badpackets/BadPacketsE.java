package integral.studios.hydro.model.check.impl.misc.badpackets;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.RotationCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.location.CustomLocation;

public class BadPacketsE extends RotationCheck {
    public BadPacketsE(PlayerData playerData) {
        super(playerData, "Bad Packets E", "Chunk Exploiting", "Incognito", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
        double deltaXZ = movementTracker.getDeltaXZ();
        double deltaY = movementTracker.getDeltaY();

        if (deltaXZ > 1.0E10 && Math.abs(deltaY) > 1.0E10) {
            handleViolation(new DetailedPlayerViolation(this,
                    "\n- §3DeltaXZ: §b" + deltaXZ
                            + "\n- §3DeltaY (ABS): §b" + Math.abs(deltaY)
            ));
        }
    }
}