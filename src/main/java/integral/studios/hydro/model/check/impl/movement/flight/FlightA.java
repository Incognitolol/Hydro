package integral.studios.hydro.model.check.impl.movement.flight;

import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public class FlightA extends PositionCheck {
    public FlightA(PlayerData playerData) {
        super(playerData, "Flight A", "Yes Check", new ViolationHandler(20, 300L), Category.MOVEMENT, SubCategory.FLIGHT);
    }

    @Override
    public void handle(CustomLocation to, CustomLocation from) {
    }
}