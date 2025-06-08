package integral.studios.hydro.model.check.impl.movement.flight;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class FlightA extends PositionCheck {
    public FlightA(PlayerData playerData) {
        super(playerData, "Flight A", "Prediction Fly Check", new ViolationHandler(20, 300L), Category.MOVEMENT, SubCategory.FLIGHT);
    }

    @Override
    public void handle() {
    }
}