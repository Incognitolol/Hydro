package integral.studios.hydro.model.check.impl.combat.aimassist;

import integral.studios.hydro.model.check.type.RotationCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class AimAssistA extends RotationCheck {
    private float suspiciousYaw;

    public AimAssistA(PlayerData playerData) {
        super(playerData, "Aim Assist A", "Rounded Yaw Check", new ViolationHandler(20, 60L), Category.COMBAT, SubCategory.AIM_ASSIST);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
        if (actionTracker.getLastAttack() > 20 * 60) {
            return;
        }

        float yawChange = Math.abs(to.getYaw() - from.getYaw());

        if (yawChange > 1F && Math.round(yawChange) == yawChange && yawChange % 1.5F != 0F) {
            if (yawChange == suspiciousYaw) {
                handleViolation(new DetailedPlayerViolation(this, "Y " + yawChange));
            }

            suspiciousYaw = Math.round(yawChange);
        } else {
            suspiciousYaw = 0F;
        }
    }
}