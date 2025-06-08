package integral.studios.hydro.model.check.impl.combat.aimassist;

import integral.studios.hydro.model.check.type.RotationCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class AimAssistA extends RotationCheck {
    private float suspiciousYaw;

    public AimAssistA(PlayerData playerData) {
        super(playerData, "Aim Assist A", "Rounded Yaw Check", ViolationHandler.EXPERIMENTAL, Category.COMBAT, SubCategory.AIM_ASSIST);
    }

    @Override
    public void handle() {
        if (actionTracker.getTicksSinceAttack() > 20 * 60) {
            return;
        }

        float yawChange = rotationTracker.getDeltaYaw();

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