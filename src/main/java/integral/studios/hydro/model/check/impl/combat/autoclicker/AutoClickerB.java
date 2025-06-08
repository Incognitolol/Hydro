package integral.studios.hydro.model.check.impl.combat.autoclicker;

import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.math.MathUtil;

import java.util.*;

public class AutoClickerB extends ArmAnimationCheck {
    public AutoClickerB(PlayerData playerData) {
        super(playerData, "Auto Clicker B", "Sparse Outliers Check", ViolationHandler.EXPERIMENTAL, false, false, 1500, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples, double cps) {
        long outliers = Arrays.stream(MathUtil.dequeTranslator(clickSamples)).filter(d -> d >= 4).count();

        if (outliers <= 5) {
            handleViolation(new DetailedPlayerViolation(this,
                    "\n- " + CC.PRI + "Outliers: " + CC.SEC + outliers +
                            "\n- " + CC.PRI + "CPS: " + CC.SEC + cps
            ));
        }
    }
}