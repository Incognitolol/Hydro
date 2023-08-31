package integral.studios.hydro.model.check.impl.combat.autoclicker.debug;

import dev.sim0n.iridium.math.statistic.Stats;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.math.MathUtil;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.bukkit.Bukkit;

import java.util.*;

public class AutoClickerDebug extends ArmAnimationCheck {
    public AutoClickerDebug(PlayerData playerData) {
        super(playerData, "Auto Clicker Debug", "Auto Clicker Debug", "Mexify", ViolationHandler.DEVELOPMENT, false, false, 150, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples, double cps) {
        if (playerData.isSniffingClicks()) {
            double kur = Stats.kurtosis(clickSamples);
            double skew = Stats.skewness(clickSamples);
            double stDev = Stats.stdDev(clickSamples);
            double mean = Stats.mean(clickSamples);
            double median = new Median().evaluate(MathUtil.dequeTranslator(clickSamples));
            double variance = Stats.variance(clickSamples);
            double entropy = Stats.entropy(clickSamples);
            double range = Stats.range(clickSamples);

            long outliers = Arrays.stream(MathUtil.dequeTranslator(clickSamples)).filter(d -> d >= 4).count();
            long zeros = Arrays.stream(MathUtil.dequeTranslator(clickSamples)).filter(d -> d == 0.0).count();

            String sb = "§b[Debug] " + playerData.getBukkitPlayer().getDisplayName() + "§7 -> " +
                    String.format("§fst=%.2f§b, ", stDev) +
                    String.format("§fsk=%.2f§b, ", skew) +
                    String.format("§fkur=%.2f§b, ", kur) +
                    String.format("§fmean=%.2f§b, ", mean) +
                    String.format("§fmedian=%.2f§b, ", median) +
                    String.format("§fvar=%.2f§b, ", variance) +
                    String.format("§fentropy=%.2f§b, ", entropy) +
                    String.format("§foutliers=%d, ", outliers) +
                    String.format("§fzeros=%d, ", zeros) +
                    String.format("§frange=%s, ", range);

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("hydro.mod.sniffclicks")).forEach(player -> player.sendMessage(CC.translate(sb)));
        }
    }
}