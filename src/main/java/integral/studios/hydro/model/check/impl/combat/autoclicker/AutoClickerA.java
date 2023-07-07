package integral.studios.hydro.model.check.impl.combat.autoclicker;

import integral.studios.hydro.model.check.type.ArmAnimationCapCheck;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public class AutoClickerA extends ArmAnimationCapCheck {
    public AutoClickerA(PlayerData playerData) {
        super(playerData, "Auto Clicker A", "High CPS Check", new ViolationHandler(5, 120L));
    }

    @Override
    public void handle(double cps) {
        if (cps > 19) {
            handleViolation(new DetailedPlayerViolation(this, "\n- §3CPS:§b " + cps));
        }
    }
}