package integral.studios.hydro.util.collisions;

import integral.studios.hydro.util.mcp.AxisAlignedBB;
import integral.studios.hydro.util.mcp.Vec3;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class SimpleCollisionBox implements CollisionBox {

    private double minX, minY, minZ, maxX, maxY, maxZ;

    public SimpleCollisionBox(double x, double y, double z) {
        minX = x;
        minY = y;
        minZ = z;
        maxX = x + 1;
        maxY = y + 1;
        maxZ = z + 1;
    }

    public SimpleCollisionBox(double minX, double minY, double minZ, double maxX,
                              double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public SimpleCollisionBox(Vec3 minPos, Vec3 maxPos, boolean solid) {
        minX = minPos.xCoord;
        minY = minPos.yCoord;
        minZ = minPos.zCoord;
        maxX = maxPos.xCoord;
        maxY = maxPos.yCoord;
        maxZ = maxPos.zCoord;
    }

    @Override
    public CollisionBox offset(double x, double y, double z) {
        minX += x;
        minY += y;
        minZ += z;
        maxX += x;
        maxY += y;
        maxZ += z;

        return this;
    }

    @Override
    public CollisionBox copy() {
        return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean isFullBlock() {
        return maxX - minX == 1 && maxY - minY == 1 && maxZ - minZ == 1;
    }

    @Override
    public boolean isCollided(AxisAlignedBB boundingBox) {
        return boundingBox.getMaxX() > this.getMinX() && boundingBox.getMinX() < this.getMaxX()
                && boundingBox.getMaxY() > this.getMinY() && boundingBox.getMinY() < this.getMaxY()
                && boundingBox.getMaxZ() > this.getMinZ() && boundingBox.getMinZ() < this.getMaxZ();
    }

    @Override
    public boolean isCollided(CollisionBox collisionBox) {
        if (collisionBox instanceof SimpleCollisionBox) {
            SimpleCollisionBox simpleCollisionBox = (SimpleCollisionBox) collisionBox;

            return simpleCollisionBox.getMaxX() > this.getMinX() && simpleCollisionBox.getMinX() < this.getMaxX()
                    && simpleCollisionBox.getMaxY() > this.getMinY() && simpleCollisionBox.getMinY() < this.getMaxY()
                    && simpleCollisionBox.getMaxZ() > this.getMinZ() && simpleCollisionBox.getMinZ() < this.getMaxZ();
        } else if (collisionBox instanceof CompositeCollisionBox) {
            for (SimpleCollisionBox simpleCollisionBox : ((CompositeCollisionBox) collisionBox).getCollisionBoxes()) {
                if (simpleCollisionBox.getMaxX() > this.getMinX() && simpleCollisionBox.getMinX() < this.getMaxX()
                        && simpleCollisionBox.getMaxY() > this.getMinY() && simpleCollisionBox.getMinY() < this.getMaxY()
                        && simpleCollisionBox.getMaxZ() > this.getMinZ() && simpleCollisionBox.getMinZ() < this.getMaxZ())
                    return true;
            }

            return false;
        }

        return false;
    }

    @Override
    public List<SimpleCollisionBox> getBoxes() {
        return new ArrayList<>(Collections.singleton(this));
    }

    // mcp shit
    public double calculateYOffset(AxisAlignedBB other, double offsetY) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetY > 0.0D && other.maxY <= this.minY) {
                double d1 = this.minY - other.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= this.maxY) {
                double d0 = this.maxY - other.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }
}
