package integral.studios.hydro.model.check.impl.combat.autoclicker;

import dev.sim0n.iridium.math.statistic.Stats;
import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.PlayerData;

import java.util.Queue;

public class AutoClickerB extends ArmAnimationCheck {
    public AutoClickerB(PlayerData playerData) {
        super(playerData, "Auto Clicker B", "Low Distribution of Kurtosis Check", ViolationHandler.EXPERIMENTAL, false, false, 750, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples) {
        double kurtosis = Stats.kurtosis(clickSamples);

        // platykurticcuh
        if (kurtosis <= 0D) {
            if (++vl > 1) {
                handleViolation(new DetailedPlayerViolation(this, "\n- §3Kurtosis: §b" + kurtosis));
            } else {
                decreaseVl(0.65D);
            }
        }
    }
}