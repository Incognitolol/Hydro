package integral.studios.hydro.service;

import integral.studios.hydro.model.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataService {
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public void registerData(Player player) {
        dataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public PlayerData getData(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public PlayerData removeData(Player player) {
        return dataMap.remove(player.getUniqueId());
    }

    public Collection<PlayerData> values() {
        return dataMap.values();
    }
}