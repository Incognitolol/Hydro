package integral.studios.hydro.model.check.impl.misc.pingspoof;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public class PingSpoofA extends PacketCheck {
    public PingSpoofA(PlayerData playerData) {
        super(playerData, "Ping Spoof A", "Yes Check", new ViolationHandler(15, 60L), Category.MISC, SubCategory.PING_SPOOF);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
    }
}