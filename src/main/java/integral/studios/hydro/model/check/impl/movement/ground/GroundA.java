package integral.studios.hydro.model.check.impl.movement.ground;

import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class GroundA extends PositionCheck {
    private int groundSpoofTicks;

    public GroundA(PlayerData playerData) {
        super(playerData, "Ground A", "Yes check", new ViolationHandler(20, 300L), Category.MOVEMENT, SubCategory.GROUND);
    }

    @Override
    public void handle() {
    }
}