package integral.studios.hydro.util.mcp;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.util.location.WrappedBlock;
import integral.studios.hydro.util.math.MathHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BoundingBox {
    private Vector min, max;

    public BoundingBox(Vector vector, double width, double height, double depth) {
        min = new Vector(vector.getX() - width, vector.getY() - depth, vector.getZ() - width);
        max = new Vector(vector.getX() + width, vector.getY() + height, vector.getZ() + width);
    }

    /**
     * Expands the bounding box by {@param amount} in every direction
     *
     * @param amount The amount to expand by
     * @return The expanded bounding box
     */
    public BoundingBox expand(double amount) {
        Vector expansion = new Vector(amount, amount, amount);

        min.subtract(expansion);
        max.add(expansion);

        return new BoundingBox(min, max);
    }

    /**
     * Expands the bounding box by {@param amount} in every direction
     *
     * @return The expanded bounding box
     */
    public BoundingBox expand(double x, double y, double z) {
        Vector expansion = new Vector(x, y, z);

        min.subtract(expansion);
        max.add(expansion);

        return new BoundingBox(min, max);
    }

    /**
     * Offsets the bounding box
     *
     * @param distance The distance to offset it by
     * @return The offset bounding box
     */
    public BoundingBox offset(Vector distance) {
        min.add(distance);
        max.add(distance);

        return new BoundingBox(min, max);
    }

    /**
     * Gets the distance to {@param location} using the min and max
     *
     * @param location The location to get the distance to
     * @return The distance
     */
    public double distance(CustomLocation location) {
        double dx = Math.min(Math.abs(location.getX() - min.getX()), Math.abs(location.getX() - max.getX()));
        double dz = Math.min(Math.abs(location.getZ() - min.getZ()), Math.abs(location.getZ() - max.getZ()));

        return dx * dx + dz * dz;
    }

    /**
     * @return A {@link AxisAlignedBB} instance of the bounding box
     */
    public AxisAlignedBB getAABB() {
        return new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public List<WrappedBlock> getBlocks(PlayerData data) {
        List<WrappedBlock> blocks = new ArrayList<>();
        for (int x = MathHelper.floor_double(min.getX()); x <= MathHelper.floor_double(max.getX()); x++) {
            for (int y = MathHelper.floor_double(min.getY()); y <= MathHelper.floor_double(max.getY()); y++) {
                for (int z = MathHelper.floor_double(min.getZ()); z <= MathHelper.floor_double(max.getZ()); z++) {
                    WrappedBlockState block = data.getWorldTracker().getBlock(x, y, z);

                    if (!block.getType().isAir())
                        blocks.add(new WrappedBlock(block.getType(), x, y, z));
                }
            }
        }
        return blocks;
    }

    public BoundingBox clone() {
        return new BoundingBox(min, max);
    }

}
