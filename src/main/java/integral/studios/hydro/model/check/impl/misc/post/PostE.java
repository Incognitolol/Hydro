package integral.studios.hydro.model.check.impl.misc.post;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.model.check.type.PostActionCheck;
import integral.studios.hydro.model.PlayerData;

public class PostE extends PostActionCheck {
    public PostE(PlayerData playerData) {
        super(playerData, "Post E", "Post Action Item Change Check",
                event -> event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE
        );
    }
}