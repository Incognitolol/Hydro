package integral.studios.hydro.model.check.impl.movement.ground;

import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.location.CustomLocation;

public class GroundA extends PositionCheck {
    public GroundA(PlayerData playerData) {
        super(playerData, "Ground A", "Yes Check", new ViolationHandler(20, 300L), Category.MOVEMENT, SubCategory.GROUND);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
    }
}