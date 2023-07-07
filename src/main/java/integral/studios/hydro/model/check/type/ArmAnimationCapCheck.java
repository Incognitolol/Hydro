package integral.studios.hydro.model.check.type;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.packet.PacketHelper;

public abstract class ArmAnimationCapCheck extends PacketCheck {
    private int cps;

    private int movements;

    public ArmAnimationCapCheck(PlayerData playerData, String name, String desc, ViolationHandler violationHandler) {
        super(playerData, name, desc, violationHandler, Category.COMBAT, SubCategory.AUTO_CLICKER);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            if (!actionTracker.isDigging()) {
                ++cps;
            }
        } else if (PacketHelper.isFlying(event.getPacketType())) {
            if (++movements >= 20) {
                if (!actionTracker.isDigging()) {
                    handle(cps);
                }

                cps = movements = 0;
            }
        }
    }

    public abstract void handle(double cps);
}
