package integral.studios.hydro.model.check;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.base.AbstractPlayerViolation;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.tracker.impl.*;
import integral.studios.hydro.service.ViolationService;
import integral.studios.hydro.util.config.ConfigurationService;
import integral.studios.hydro.model.tracker.impl.*;
import lombok.Getter;
import lombok.Setter;
import org.atteo.classindex.IndexSubclasses;

@Getter
@Setter
@IndexSubclasses
public abstract class Check {
    protected final ViolationService violationService = Hydro.get(ViolationService.class);

    protected final TransactionTracker transactionTracker;
    protected final KeepAliveTracker keepAliveTracker;
    protected final CollisionTracker collisionTracker;
    protected final AttributeTracker attributeTracker;
    protected final PositionTracker positionTracker;
    protected final RotationTracker rotationTracker;
    protected final TeleportTracker teleportTracker;
    protected final VelocityTracker velocityTracker;
    protected final EntityTracker entityTracker;
    protected final ActionTracker actionTracker;

    protected final PlayerData playerData;
    private final String name, desc;

    private final ViolationHandler violationHandler;

    private final Category category;

    private final SubCategory subCategory;

    protected double vl = 0D;




    protected Check(PlayerData playerData, String name, String desc, ViolationHandler violationHandler, Category category, SubCategory subCategory) {
        this.playerData = playerData;
        this.name = name;
        this.desc = desc;
        this.violationHandler = violationHandler;
        this.category = category;
        this.subCategory = subCategory;

        transactionTracker = playerData.getTransactionTracker();
        keepAliveTracker = playerData.getKeepAliveTracker();
        collisionTracker = playerData.getCollisionTracker();
        attributeTracker = playerData.getAttributeTracker();
        positionTracker = playerData.getPositionTracker();
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

    public boolean isEnabled() {
        return Hydro.get(ConfigurationService.class).isEnabled(this.getClass().getSimpleName());
    }

    public boolean isCanAutoban() {
        return Hydro.get(ConfigurationService.class).isAutoban(this.getClass().getSimpleName());
    }

    public double getMaxViolations() {
        return Hydro.get(ConfigurationService.class).getMaxViolations(this.getClass().getSimpleName());
    }

    protected String format(int places, Object obj) {
        return String.format("%." + places + "f", obj);
    }
}