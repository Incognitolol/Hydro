package integral.studios.hydro.model.check;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.impl.*;
import integral.studios.hydro.service.ViolationService;
import integral.studios.hydro.model.check.violation.base.AbstractPlayerViolation;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import lombok.Getter;
import org.atteo.classindex.IndexSubclasses;

@Getter
@IndexSubclasses
public abstract class Check {
    protected final ViolationService violationService = Hydro.get(ViolationService.class);

    protected final MovementEmulationTracker movementEmulationTracker;
    protected final TransactionTracker transactionTracker;
    protected final KeepAliveTracker keepAliveTracker;
    protected final CollisionTracker collisionTracker;
    protected final AttributeTracker attributeTracker;
    protected final MovementTracker movementTracker;
    protected final RotationTracker rotationTracker;
    protected final TeleportTracker teleportTracker;
    protected final VelocityTracker velocityTracker;
    protected final EntityTracker entityTracker;
    protected final ActionTracker actionTracker;

    protected final PlayerData playerData;
    private final String name, desc, credits;

    private final ViolationHandler violationHandler;

    private final Category category;

    private final SubCategory subCategory;

    protected double vl = 0D;

    protected Check(PlayerData playerData, String name, String desc, String credits, ViolationHandler violationHandler, Category category, SubCategory subCategory) {
        this.playerData = playerData;
        this.name = name;
        this.desc = desc;
        this.credits = credits;
        this.violationHandler = violationHandler;
        this.category = category;
        this.subCategory = subCategory;

        movementEmulationTracker = playerData.getMovementEmulationTracker();
        transactionTracker = playerData.getTransactionTracker();
        keepAliveTracker = playerData.getKeepAliveTracker();
        collisionTracker = playerData.getCollisionTracker();
        attributeTracker = playerData.getAttributeTracker();
        movementTracker = playerData.getMovementTracker();
        rotationTracker = playerData.getRotationTracker();
        teleportTracker = playerData.getTeleportTracker();
        velocityTracker = playerData.getVelocityTracker();
        entityTracker = playerData.getEntityTracker();
        actionTracker = playerData.getActionTracker();
    }

    public void handleViolation(AbstractPlayerViolation violation) {
        violationService.handleViolation(violation);
    }

    protected void decreaseVl(double decrease) {
        vl = Math.max(vl - decrease, 0D);
    }

    public void resetVl() {
        vl = 0D;
    }

    protected String format(int places, Object obj) {
        return String.format("%." + places + "f", obj);
    }
}