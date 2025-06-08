package integral.studios.hydro.model.check.impl.combat.autoclicker;

import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.math.MathUtil;

import java.util.Queue;

public class AutoClickerC extends ArmAnimationCheck {
    public AutoClickerC(PlayerData playerData) {
        super(playerData, "Auto Clicker C", "Negative Kurtosis Check", ViolationHandler.EXPERIMENTAL, false, false, 750, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples, double cps) {
        double kurtosis = MathUtil.getKurtosis(clickSamples);

        // Kurtosis
        if (kurtosis <= 0) {
            handleViolation(new DetailedPlayerViolation(this,
                    "\n- " + CC.PRI + "Kurtosis: " + CC.SEC + kurtosis +
                            "\n- " + CC.PRI + "CPS: " + CC.SEC + cps));
        }
    }
}