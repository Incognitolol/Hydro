package integral.studios.hydro.model.check.impl.misc.post;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PostActionCheck;

public class PostA extends PostActionCheck {
    public PostA(PlayerData playerData) {
        super(playerData, "Post A", "Post Action Arm Animation Check",
                event -> event.getPacketType() == PacketType.Play.Client.ANIMATION
        );
    }
}