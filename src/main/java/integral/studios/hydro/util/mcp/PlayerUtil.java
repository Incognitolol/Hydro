package integral.studios.hydro.util.mcp;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.math.MathHelper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlayerUtil {

    public AxisAlignedBB getBoundingBox(double x, double y, double z) {
        float width = 0.6F / 2.0F;

        return new AxisAlignedBB(x - width, y, z - width, x + width, y + 1.8F, z + width);
    }

    public boolean isInWater(PlayerData data) {
        AxisAlignedBB bb = getBoundingBox(data.getPositionTracker().getX(),
                data.getPositionTracker().getY(), data.getPositionTracker().getZ())
                .expand(0, -0.4000000059604645D, 0)
                .contract(0.001, 0.001, 0.001);

        for (int x = MathHelper.floor_double(bb.minX); x <= MathHelper.floor_double(bb.maxX); x++) {
            for (int y = MathHelper.floor_double(bb.minY); y <= MathHelper.floor_double(bb.maxY); y++) {
                for (int z = MathHelper.floor_double(bb.minZ); z <= MathHelper.floor_double(bb.maxZ); z++) {
                    WrappedBlockState block = data.getWorldTracker().getBlock(x, y, z);

                    if (block.getType() == StateTypes.WATER)
                        return true;
                }
            }
        }

        return false;
    }

    public boolean isInLava(PlayerData data) {
        AxisAlignedBB bb = getBoundingBox(data.getPositionTracker().getX(),
                data.getPositionTracker().getY(), data.getPositionTracker().getZ())
                .expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);

        for (int x = MathHelper.floor_double(bb.minX); x <= MathHelper.floor_double(bb.maxX); x++) {
            for (int y = MathHelper.floor_double(bb.minY); y <= MathHelper.floor_double(bb.maxY); y++) {
                for (int z = MathHelper.floor_double(bb.minZ); z <= MathHelper.floor_double(bb.maxZ); z++) {
                    WrappedBlockState block = data.getWorldTracker().getBlock(x, y, z);

                    if (block.getType() == StateTypes.LAVA)
                        return true;
                }
            }
        }

        return false;
    }
}
