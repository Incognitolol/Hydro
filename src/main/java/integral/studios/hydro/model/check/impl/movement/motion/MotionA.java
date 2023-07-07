package integral.studios.hydro.model.check.impl.movement.motion;

import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public class MotionA extends PositionCheck {
    public MotionA(PlayerData playerData) {
        super(playerData, "Motion A", "Yes Check", new ViolationHandler(15, 300L), Category.MOVEMENT, SubCategory.MOTION);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
    }
}