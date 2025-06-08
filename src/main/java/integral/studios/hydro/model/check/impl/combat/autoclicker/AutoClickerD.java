package integral.studios.hydro.model.check.impl.combat.autoclicker;

import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.math.MathUtil;

import java.util.Queue;

public class AutoClickerD extends ArmAnimationCheck {
    public AutoClickerD(PlayerData playerData) {
        super(playerData, "Auto Clicker D", "Low Randomization Spread Check", ViolationHandler.EXPERIMENTAL, false, false, 800, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples, double cps) {
        double standardDeviation = MathUtil.getStandardDeviation(clickSamples);

        // Check for low randomization spread
        if (standardDeviation < 0.5) {
            handleViolation(new DetailedPlayerViolation(this,
                    "\n- " + CC.PRI + "STD: " + CC.SEC + standardDeviation+
                            "\n- " + CC.PRI + "CPS: " + CC.SEC + cps));
        }
    }
}