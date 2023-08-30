package integral.studios.hydro.model.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class BadPacketsF extends PacketCheck {
    public BadPacketsF(PlayerData playerData) {
        super(playerData, "Bad Packets F", "Large Y Position", "Incognito", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            if (Math.abs(flying.getLocation().getY()) > 1.0E9) {
                handleViolation(new DetailedPlayerViolation(this, "\n- §3Y: §b" + flying.getLocation().getY()));
            }
        }
    }
}
