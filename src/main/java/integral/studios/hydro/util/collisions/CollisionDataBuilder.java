package integral.studios.hydro.util.collisions;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import integral.studios.hydro.model.PlayerData;

public interface CollisionDataBuilder {

    CollisionBox fetch(PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}
