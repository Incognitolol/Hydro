package integral.studios.hydro.model.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.PlayerViolation;
import integral.studios.hydro.util.packet.PacketHelper;

public class BadPacketsB extends PacketCheck {
    private boolean sent = false;

    public BadPacketsB(PlayerData playerData) {
        super(playerData, "Bad Packets B", "Double Sprint or Sneak Check", "", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (teleportTracker.isTeleporting()) {
            sent = false;
        }

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction entityAction = new WrapperPlayClientEntityAction(event);

            if (entityAction.getAction().name().toLowerCase().contains("sprint") || entityAction.getAction().name().toLowerCase().contains("sneak"))
                sent = true;

        } else if (PacketHelper.isFlying(event.getPacketType())) {
            sent = false;
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            if (sent) handleViolation(new PlayerViolation(this));
        }
    }
}