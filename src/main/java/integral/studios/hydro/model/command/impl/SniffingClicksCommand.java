package integral.studios.hydro.model.command.impl;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.command.framework.BaseCommand;
import integral.studios.hydro.model.command.framework.CommandManifest;
import integral.studios.hydro.service.PlayerDataService;
import integral.studios.hydro.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandManifest(permission = "hydro.mod.sniffclicks")
public class SniffingClicksCommand extends BaseCommand {
    public SniffingClicksCommand() {
        super("sniffclicks");
    }

    @Override
    public void handle(Player player, List<String> args) {
        if (args.size() < 1) {
            sendMessage(player, CC.RED + "Usage: /sniffclicks <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args.get(0));

        if (target == null) {
            sendMessage(player, String.format("%sThat player could not be found.", CC.RED));
            return;
        }

        PlayerData targetData = Hydro.get(PlayerDataService.class).getData(target);

        targetData.setSniffingClicks(!targetData.isSniffingClicks());

        sendMessage(player, targetData.isSniffingClicks() ? CC.GREEN + target.getName() + " clicks' are now being sniffed."
                : CC.RED + target.getName() + " clicks' are now no longer being sniffed.");
    }
}