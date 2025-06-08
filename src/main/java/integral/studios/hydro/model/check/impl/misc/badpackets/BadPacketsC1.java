package integral.studios.hydro.model.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class BadPacketsC1 extends PacketCheck {
    public BadPacketsC1(PlayerData playerData) {
        super(playerData, "Bad Packets C1", "Illegal Payload Name", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage payload = new WrapperPlayClientPluginMessage(event);

            if (payload.getChannelName().equals("Vanilla")) {
                handleViolation(new DetailedPlayerViolation(this, "\n- " + CC.PRI + "CB: " + CC.SEC + payload.getChannelName()));
            }
        }
    }
}