package integral.studios.hydro.model.check.type;

import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public abstract class PositionCheck extends Check {
    public PositionCheck(PlayerData playerData, String name, String desc, ViolationHandler violationHandler, Category category, SubCategory subCategory) {
        super(playerData, name, desc, violationHandler, category, subCategory);
    }

    public abstract void handle();
}