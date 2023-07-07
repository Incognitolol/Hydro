package integral.studios.hydro.model.check.impl.misc.post;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import integral.studios.hydro.model.check.type.PostActionCheck;
import integral.studios.hydro.model.PlayerData;

public class PostC extends PostActionCheck {
    public PostC(PlayerData playerData) {
        super(playerData, "Post C", "Post Action Attack Check",
                event -> {
                    if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
                        WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

                        return useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK;
                    }

                    return false;
                });
    }
}