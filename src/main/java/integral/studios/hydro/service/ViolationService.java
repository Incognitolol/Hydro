package integral.studios.hydro.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.violation.log.Log;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.base.AbstractPlayerViolation;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.model.check.violation.log.ViolationLog;
import integral.studios.hydro.model.check.violation.punishment.PlayerBan;
import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViolationService {
    @Getter
    private final Set<UUID> alertsEnabled = new HashSet<>();

    private static final String VIOLATION_ALERT_FORMAT = Hydro.PREFIX + "&b%s &7failed &b%s &7[VL: %d]";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setPriority(3)
                    .setNameFormat("Hydro Violation Executor Thread")
                    .build()
    );

    public void handleViolation(AbstractPlayerViolation violation) {
        executorService.submit(() -> {
            Check check = violation.getCheck();
            PlayerData playerData = check.getPlayerData();

            if (playerData.isBanning()) {
                return;
            }

            playerData.addViolation(check, new ViolationLog(violation.getPoints()));

            int violations = playerData.getViolationLevel(check);

            String playerName = playerData.getBukkitPlayer().getName();
            String checkName = check.getName();
            String desc = check.getDesc();

            ViolationHandler handler = check.getViolationHandler();

            boolean experimental = handler == ViolationHandler.EXPERIMENTAL;

            String data = violation instanceof DetailedPlayerViolation ? "" + violation.getData() : "";

            String alert = ChatColor.translateAlternateColorCodes('&', String.format(
                    VIOLATION_ALERT_FORMAT,
                    playerName,
                    checkName + (experimental ? " &c(Experimental)" : ""),
                    violations
            ));

            TextComponent textComponent = new TextComponent(alert);

            for (UUID uuid : alertsEnabled) {
                if (Bukkit.getPlayer(uuid).isOp()) {
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                            "§3Desc: §b" + desc
                                    + "\n§3Statistics: §r" + data
                    ).create()));
                } else {
                    textComponent.setHoverEvent(null);
                }
            }

            int violationPoints = playerData.getViolationLevel(check, handler.getMaxViolationTimeLength() * 1000);

            if (!experimental && violationPoints > handler.getMaxViolations()) {
                if (Hydro.get().isLagging()) {
                    Bukkit.broadcastMessage(playerName + " would have got banned for " + checkName + " VL: " + violations + " but server lagged");
                    return;
                }

                //handleBan(new PlayerBan(playerData, String.format("%s VL %d", checkName, violations)));
                Bukkit.broadcastMessage(playerName + " would have got banned for " + checkName + " VL: " + violations);
            }

            for (UUID uuid : alertsEnabled) {
                Bukkit.getPlayer(uuid).spigot().sendMessage(textComponent);
            }

            if (!Hydro.get().getConfiguration().isDatabaseEnabled()) {
                return;
            }

            Log log = new Log(playerData.getBukkitPlayer().getUniqueId(), checkName, data, violationPoints);

            Hydro.get(LogService.class).getQueuedLogs().add(log);
        });
    }

    public void handleBan(PlayerBan ban) {
        PlayerData playerData = ban.getPlayerData();

        if (playerData.isBanning()) {
            return;
        }

        playerData.setBanning(true);

        String playerName = playerData.getBukkitPlayer().getName();

        Hydro.get().getServer().getScheduler().runTask(Hydro.get(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("kick %s %s", playerName, ban.getReason()));
        });
    }
}