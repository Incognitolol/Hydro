package integral.studios.hydro.model.check.impl.misc.post;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.model.check.type.PostActionCheck;
import integral.studios.hydro.model.PlayerData;

public class PostB extends PostActionCheck {
    public PostB(PlayerData playerData) {
        super(playerData, "Post B", "Post Action Block Place Check",
                event -> event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT
        );
    }
}