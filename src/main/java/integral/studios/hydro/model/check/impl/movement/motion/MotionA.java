package integral.studios.hydro.model.check.impl.movement.motion;

import com.github.retrooper.packetevents.util.Vector3d;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.model.check.type.PositionCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;

public class MotionA extends PositionCheck {
    public MotionA(PlayerData playerData) {
        super(playerData, "Motion A", "Step Motion Check", new ViolationHandler(15, 300L), Category.MOVEMENT, SubCategory.MOTION);
    }

    @Override
    public void handle() {
    }
}