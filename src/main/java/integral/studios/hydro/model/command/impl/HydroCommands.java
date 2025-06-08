package integral.studios.hydro.model.command.impl;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.violation.log.Log;
import integral.studios.hydro.model.check.violation.punishment.PlayerBan;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.command.HydroCommand;
import integral.studios.hydro.model.gui.impl.MainGUI;
import integral.studios.hydro.service.*;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.http.HttpUtil;
import integral.studios.hydro.service.*;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("hydro")
public class HydroCommands extends HydroCommand {
    @Default
    @HelpCommand
    @Syntax("[page]")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("alerts")
    @CommandAlias("alerts")
    @Description("Toggles your anti-cheat alerts")
    @CommandPermission("hydro.alerts")
    public void onAlerts(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return;
        }

        Player player = ((Player) sender).getPlayer();
        Set<UUID> alertsEnabled = Hydro.get(ViolationService.class).getAlertsEnabled();

        UUID uuid = player.getUniqueId();

        if (!alertsEnabled.remove(uuid)) {
            alertsEnabled.add(uuid);
        }

        boolean hasAlerts = alertsEnabled.contains(uuid);

        player.sendMessage(CC.SEC + CC.DOT + CC.SEC + " You have " + (hasAlerts ? CC.GREEN + "subscribed" : CC.RED + "unsubscribed") + CC.SEC + " to " + CC.PRI + "Hydro" + CC.SEC + " alerts");
    }

    @Subcommand("gui")
    @Description("Opens the GUI")
    @CommandPermission("hydro.gui")
    public void onGUI(Player player) {
        Hydro.get(GuiService.class).getByClass(MainGUI.class).openToPlayer(player);

    }

    @Subcommand("info")
    @Description("Display info on a player")
    @Syntax("<target>")
    @CommandPermission(value = "hydro.info")
    public void onInfo(Player player, OnlinePlayer onlinePlayer) {
        Player target = onlinePlayer.player;
        PlayerData data = Hydro.get(PlayerDataService.class).getData(target);

        final List<String> message = Arrays.asList(
                CC.PRI + "&lHydro Information",
                " ",
                "&7Name: &f" + target.getDisplayName(),
                "&7Tracked Entities: &f" + data.getEntityTracker().getEntityMap().size(),
                "&7Sensitivity: &f" + data.getRotationTracker().getSensitivity() + "%",
                "&7Keep Alive Ping: &f" + data.getKeepAliveTracker().getKeepAlivePing() + "ms"
        );

        sendLineBreak(player);
        sendMessage(player, String.join("\n", message));
        sendLineBreak(player);
    }

    @Subcommand("logs")
    @CommandAlias("logs")
    @Description("Display past violations of a player")
    @Syntax("<target>")
    @CommandCompletion("@players")
    @CommandPermission("hydro.logs")
    public void onLogs(Player player, OfflinePlayer target) {
        long now = System.currentTimeMillis();

        UUID uuid = target.getUniqueId();

        Iterable<Log> logs = Hydro.get(DataBaseService.class).getLogsCollection()
                .find(Filters.eq("uuid", uuid.toString()))
                .sort(Indexes.descending("_id"))
                .map(Log::fromDocument);

        Map<String, Integer> checkCounts = new HashMap<>();
        Set<String> uniqueChecks = new HashSet<>();

        Hydro.get(LogService.class).getQueuedLogs().stream()
                .filter(log -> log.getUuid().equals(uuid))
                .forEach(log -> {
                    String check = log.getCheck();
                    int count = checkCounts.getOrDefault(check, 0) + 1;
                    checkCounts.put(check, count);
                });

        List<BaseComponent> logComponents = new ArrayList<>();

        logs.forEach(log -> {
            String check = log.getCheck();

            int count = checkCounts.getOrDefault(check, 0) + 1;

            checkCounts.put(check, count);
        });

        if (!logs.iterator().hasNext()) {
            sendMessage(player, CC.RED + "No logs found for " + target.getName());
            return;
        }

        for (Log log : logs) {
            String check = log.getCheck();

            if (!uniqueChecks.contains(check)) {
                String logMessage = createLogMessage(log, now, checkCounts.get(check));

                TextComponent logComponent = new TextComponent(logMessage);

                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Log Data for " + check + ":\n" + log.getData()).create());

                logComponent.setHoverEvent(hoverEvent);

                logComponents.add(logComponent);
                uniqueChecks.add(check);
            }
        }

        TextComponent textComponent = new TextComponent("");

        logComponents.forEach(textComponent::addExtra);

        String key = HttpUtil.getPastie(textComponent.toPlainText());

        if (key == null) {
            sendMessage(player, CC.RED + "An error occurred while uploading the logs.");
            return;
        }

        player.sendMessage(CC.PRI + target.getName() + CC.SEC + "'s Hydro Logs:");
        player.sendMessage("");
        logComponents.forEach(logComponent -> player.spigot().sendMessage(logComponent));
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Paste Logs: " + ChatColor.WHITE + "https://paste.md-5.net/" + key);
    }

    private String createLogMessage(Log log, long now, int flagCount) {
        String timeAgo = "[" + DurationFormatUtils.formatDuration(now - log.getTimestamp(), "dd:HH:mm:ss") + "]";
        String check = log.getCheck();

        return ChatColor.GRAY + timeAgo + " " + CC.SEC + check + ChatColor.GRAY + " > " + CC.PRI + "x" + flagCount;
    }

    @Subcommand("sniffclicks")
    @Description("Display values from a players clicks")
    @Syntax("<target>")
    @CommandPermission("hydro.sniffclicks")
    public void onSniff(Player player, OnlinePlayer onlinePlayer) {
        Player target = onlinePlayer.player;

        if (target == null) {
            sendMessage(player, String.format("%sThat player could not be found.", CC.RED));
            return;
        }

        PlayerData targetData = Hydro.get(PlayerDataService.class).getData(target);

        targetData.setSniffingClicks(!targetData.isSniffingClicks());

        sendMessage(player, targetData.isSniffingClicks() ? CC.GREEN + target.getName() + " clicks' are now being sniffed."
                : CC.RED + target.getName() + " clicks' are now no longer being sniffed.");
    }

    @Subcommand("ban")
    @Description("Ban a player through Hydro")
    @Syntax("<target>")
    @CommandPermission("hydro.ban")
    public void onBan(Player player, OnlinePlayer onlinePlayer) {
        Player target = onlinePlayer.player;
        PlayerData targetData = Hydro.get(PlayerDataService.class).getData(target);

        player.sendMessage(ChatColor.YELLOW + "You have manually banned " + ChatColor.WHITE + target.getName() + ChatColor.YELLOW + " from the network.");

        Hydro.get(ViolationService.class).handleBan(new PlayerBan(targetData, "Unfair Advantage"));
    }

    @Subcommand("togglebans")
    @Description("Toggle Hydro's auto bans")
    @CommandPermission("hydro.togglebans")
    public void onToggleBans(Player player) {
        Hydro.get().getConfiguration().setAutoBans(!Hydro.get().getConfiguration().isAutoBans());

        player.sendMessage(CC.SEC + "You have " + (Hydro.get().getConfiguration().isAutoBans() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + CC.SEC + " auto bans.");
    }
}