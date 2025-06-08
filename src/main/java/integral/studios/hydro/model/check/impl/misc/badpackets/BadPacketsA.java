package integral.studios.hydro.model.check.impl.misc.badpackets;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.RotationCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class BadPacketsA extends RotationCheck {
    public BadPacketsA(PlayerData playerData) {
        super(playerData, "Bad Packets A", "Impossible Pitch Check", new ViolationHandler(1, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle() {
        if (teleportTracker.isTeleporting()) {
            return;
        }

        float pitch = Math.abs(rotationTracker.getPitch());

        if (pitch > 90F) {
            handleViolation(new DetailedPlayerViolation(this, pitch));
        }
    }
}