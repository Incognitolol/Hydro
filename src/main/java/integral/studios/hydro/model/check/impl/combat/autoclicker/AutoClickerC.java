package integral.studios.hydro.model.check.impl.combat.autoclicker;

import dev.sim0n.iridium.math.statistic.Stats;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

import java.util.Queue;

public class AutoClickerC extends ArmAnimationCheck {
    public AutoClickerC(PlayerData playerData) {
        super(playerData, "Auto Clicker C", "Low Randomization Spread Check", ViolationHandler.EXPERIMENTAL, false, false, 800, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples, double cps) {
        double standardDeviation = Stats.stdDev(clickSamples);

        // Check for low randomization spread
        if (standardDeviation < 0.5) {
            handleViolation(new DetailedPlayerViolation(this, "\n§3STD: §b" + standardDeviation+ "\n- §3CPS: §b" + cps));
        }
    }
}