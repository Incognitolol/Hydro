package integral.studios.hydro.model.check.impl.misc.post;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PostActionCheck;

public class PostD extends PostActionCheck {
    public PostD(PlayerData playerData) {
        super(playerData, "Post D", "Post Action Use Entity Check",
                event -> event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES
        );
    }
}