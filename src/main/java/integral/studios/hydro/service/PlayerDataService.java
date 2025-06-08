package integral.studios.hydro.service;

import com.github.retrooper.packetevents.protocol.player.User;
import integral.studios.hydro.model.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataService {
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public void registerData(User user) {
        dataMap.put(user.getUUID(), new PlayerData(user));
    }

    public PlayerData getData(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public PlayerData removeData(UUID uuid) {
        return dataMap.remove(uuid);
    }
}