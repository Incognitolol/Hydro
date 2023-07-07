package integral.studios.hydro.model.command.impl;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.command.framework.BaseCommand;
import integral.studios.hydro.model.command.framework.CommandManifest;
import integral.studios.hydro.service.ViolationService;
import integral.studios.hydro.util.chat.CC;
import org.bukkit.entity.Player;

import java.util.*;

@CommandManifest(permission = "hydro.mod.alerts")
public class AlertsCommand extends BaseCommand {
    public AlertsCommand() {
        super("alerts");
    }

    @Override
    public void handle(Player player, List<String> args) {
        Set<UUID> alertsEnabled = Hydro.get(ViolationService.class).getAlertsEnabled();

        UUID uuid = player.getUniqueId();

        if (!alertsEnabled.remove(uuid)) {
            alertsEnabled.add(uuid);
        }

        boolean hasAlerts = alertsEnabled.contains(uuid);

        sendMessage(player, String.format("%sYou%s have toggled alerts %s%s.",
                CC.YELLOW, CC.GRAY, hasAlerts ? CC.GREEN + "ON" : CC.RED + "OFF", CC.GRAY));
    }
}