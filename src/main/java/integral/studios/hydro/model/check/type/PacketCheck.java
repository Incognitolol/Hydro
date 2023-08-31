package integral.studios.hydro.model.check.type;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public abstract class PacketCheck extends Check {
    public PacketCheck(PlayerData playerData, String name, String desc, String credits, ViolationHandler violationHandler, Category category, SubCategory subCategory) {
        super(playerData, name, desc, credits, violationHandler, category, subCategory);
    }

    public abstract void handle(PacketReceiveEvent event);
}
