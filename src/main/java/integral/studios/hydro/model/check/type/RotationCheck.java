package integral.studios.hydro.model.check.type;

import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public abstract class RotationCheck extends Check {
    public RotationCheck(PlayerData playerData, String name, String desc, String credits, ViolationHandler violationHandler, Category category, SubCategory subCategory) {
        super(playerData, name, desc, credits, violationHandler, category, subCategory);
    }

    public abstract void handle(CustomLocation to, CustomLocation from);
}
