package integral.studios.hydro.model.check.impl.combat.autoclicker.debug;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.check.type.ArmAnimationCheck;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.util.math.MathUtil;
import org.bukkit.Bukkit;

import java.util.*;

public class AutoClickerDebug extends ArmAnimationCheck {
    public AutoClickerDebug(PlayerData playerData) {
        super(playerData, "Auto Clicker Debug", "Auto Clicker Debug", ViolationHandler.DEVELOPMENT, false, false, 150, 8);
    }

    @Override
    public void handle(Queue<Integer> clickSamples, double cps) {
        if (playerData.isSniffingClicks()) {
            double kur = MathUtil.getKurtosis(clickSamples);
            double skew = MathUtil.getSkewness(clickSamples);
            double stDev = MathUtil.getStandardDeviation(clickSamples);
            double median = MathUtil.getMedian(clickSamples);
            double mode = MathUtil.getMode(clickSamples);
            double variance = MathUtil.getVariance(clickSamples);

            long zeros = Arrays.stream(MathUtil.dequeTranslator(clickSamples)).filter(d -> d == 0.0).count();


            String sb = "§b[Debug] " + playerData.getBukkitPlayer().getDisplayName() + "§7 -> " +
                    String.format("§fst=%.2f§b, ", stDev) +
                    String.format("§fsk=%.2f§b, ", skew) +
                    String.format("§fkur=%.2f§b, ", kur) +
                    String.format("§fmedian=%.2f§b, ", median) +
                    String.format("§fmode=%s§b, ", mode) +
                    String.format("§fvar=%.2f§b, ", variance) +
                    String.format("§fzeros=%d§b, ", zeros) +
                    String.format("§fcps=%s§b, ", cps);

            Bukkit.getOnlinePlayers().stream().filter(player ->
                    player.hasPermission("hydro.mod.sniffclicks")).forEach(player -> player.sendMessage(CC.translate(sb)));
        }
    }
}