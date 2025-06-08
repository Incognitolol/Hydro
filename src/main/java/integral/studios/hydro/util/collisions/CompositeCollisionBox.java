package integral.studios.hydro.util.collisions;

import integral.studios.hydro.util.mcp.AxisAlignedBB;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class CompositeCollisionBox implements CollisionBox {

    private final List<SimpleCollisionBox> collisionBoxes;

    public CompositeCollisionBox() {
        collisionBoxes = new ArrayList<>();
    }

    public CompositeCollisionBox(SimpleCollisionBox... collisionBoxes) {
        this.collisionBoxes = Arrays.asList(collisionBoxes);
    }

    public CompositeCollisionBox(List<SimpleCollisionBox> collisionBoxes) {
        this.collisionBoxes = collisionBoxes;
    }

    public void add(SimpleCollisionBox collisionBox) {
        collisionBoxes.add(collisionBox);
    }

    @Override
    public CollisionBox copy() {
        CompositeCollisionBox compositeCollisionBox = new CompositeCollisionBox();

        for (SimpleCollisionBox collisionBox : collisionBoxes) {
            compositeCollisionBox.add((SimpleCollisionBox) collisionBox.copy());
        }

        return compositeCollisionBox;
    }

    @Override
    public CollisionBox offset(double x, double y, double z) {
        collisionBoxes.forEach(collisionBox -> collisionBox.offset(x, y, z));

        return this;
    }

    @Override
    public boolean isFullBlock() {
        return false;
    }

    @Override
    public boolean isCollided(AxisAlignedBB bb) {
        for (SimpleCollisionBox simpleCollisionBox : collisionBoxes) {
            if (simpleCollisionBox.isCollided(bb))
                return true;
        }

        return false;
    }

    @Override
    public boolean isCollided(CollisionBox collisionBox) {
        if (collisionBox instanceof SimpleCollisionBox) {
            SimpleCollisionBox simpleCollisionBox = (SimpleCollisionBox) collisionBox;

            for (SimpleCollisionBox simpleCollisionBox1 : collisionBoxes) {
                if (simpleCollisionBox1.isCollided(simpleCollisionBox))
                    return true;
            }

            return false;
        } else if (collisionBox instanceof CompositeCollisionBox) {
            for (SimpleCollisionBox simpleCollisionBox : ((CompositeCollisionBox) collisionBox).getCollisionBoxes()) {
                for (SimpleCollisionBox simpleCollisionBox1 : collisionBoxes) {
                    if (simpleCollisionBox1.isCollided(simpleCollisionBox))
                        return true;
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public List<SimpleCollisionBox> getBoxes() {
        return collisionBoxes;
    }

    @Override
    public double calculateYOffset(AxisAlignedBB other, double offsetY) {
        for (SimpleCollisionBox box : collisionBoxes) {
            offsetY = box.calculateYOffset(other, offsetY);
        }

        return offsetY;
    }
}
