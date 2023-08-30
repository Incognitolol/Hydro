package integral.studios.hydro.model.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.math.MathUtil;

public class BadPacketsG extends PacketCheck {
    public BadPacketsG(PlayerData playerData) {
        super(playerData, "Bad Packets G", "Invalid SteerVehicle packets.", "Incognito", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle steerVehicle = new WrapperPlayClientSteerVehicle(event);

            float forward = Math.abs(steerVehicle.getForward());
            float side = Math.abs(steerVehicle.getSideways());

            boolean invalid = forward > .98F || side > .98F;

            if (invalid) {
                handleViolation(new DetailedPlayerViolation(this,
                        "\n- §3Forward: §b" + forward
                                + "\n- §3Sideways: §b" + side));
            }
        }
    }
}
