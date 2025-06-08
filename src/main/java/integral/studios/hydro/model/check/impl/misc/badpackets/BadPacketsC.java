package integral.studios.hydro.model.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.chat.CC;

public class BadPacketsC extends PacketCheck {
    public BadPacketsC(PlayerData playerData) {
        super(playerData, "Bad Packets C", "Illegal Payload Size", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage payload = new WrapperPlayClientPluginMessage(event);

            if (payload.getChannelName().length() > 1500) {
                handleViolation(new DetailedPlayerViolation(this,
                        "\n- " + CC.PRI + "Payload Size: " + CC.SEC + payload.getChannelName().length()));
            }
        }
    }
}