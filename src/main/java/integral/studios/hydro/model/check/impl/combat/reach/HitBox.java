package integral.studios.hydro.model.check.impl.combat.reach;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;

public class HitBox extends Check {
    public HitBox(PlayerData playerData) {
        super(playerData, "HitBox", "Modification Of Hit Box Check", "Mexify", new ViolationHandler(20, 160L), Category.COMBAT, SubCategory.REACH);
    }
}
