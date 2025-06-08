package integral.studios.hydro.model.emulator.handler.impl;

import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.emulator.handler.Handler;
import integral.studios.hydro.util.math.MathHelper;
import integral.studios.hydro.util.mcp.AxisAlignedBB;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.emulator.data.BruteforceData;
import integral.studios.hydro.model.emulator.data.EmulationData;

import java.util.ArrayList;
import java.util.List;

public class MoveEntityHandler implements Handler {
    @Override
    public EmulationData process(BruteforceData data, PlayerData playerData, EmulationData emulationData) {
        BaseChunk chunk = playerData.getWorldTracker().getChunk(
                playerData.getPositionTracker().getX(),
                playerData.getPositionTracker().getY(),
                playerData.getPositionTracker().getZ()
        );

        double x = emulationData.getX();
        double y = emulationData.getY();
        double z = emulationData.getZ();

        if (playerData.getCollisionTracker().isLastInWeb()) {
            x *= 0.25D;
            y *= 0.05000000074505806D;
            z *= 0.25D;

            emulationData.setX(0.0D);
            emulationData.setY(0.0D);
            emulationData.setZ(0.0D);
        }

        double d3 = x;
        double d4 = y;
        double d5 = z;

        boolean flag = data.isGround() && data.isSneaking();

        AxisAlignedBB lastBoundingBox = playerData.getCollisionTracker().getLastBoundingBox();

        if (flag) {
            double d6;

            for (d6 = 0.05D; x != 0.0D && getCollidingBoundingBoxes(playerData, chunk, lastBoundingBox.offset(x, -1.0D, 0.0D)).isEmpty(); d3 = x) {
                if (x < d6 && x >= -d6) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= d6;
                } else {
                    x += d6;
                }
            }

            for (; z != 0.0D && getCollidingBoundingBoxes(playerData, chunk, lastBoundingBox.offset(0.0D, -1.0D, z)).isEmpty(); d5 = z) {
                if (z < d6 && z >= -d6) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= d6;
                } else {
                    z += d6;
                }
            }

            for (; x != 0.0D && z != 0.0D && getCollidingBoundingBoxes(playerData, chunk, lastBoundingBox.offset(x, -1.0D, z)).isEmpty(); d5 = z) {
                if (x < d6 && x >= -d6) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= d6;
                } else {
                    x += d6;
                }

                d3 = x;

                if (z < d6 && z >= -d6) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= d6;
                } else {
                    z += d6;
                }
            }
        }

        List<AxisAlignedBB> list1 = getCollidingBoundingBoxes(playerData, chunk, lastBoundingBox.addCoord(x, y, z));

        for (AxisAlignedBB bb : list1) {
            y = bb.calculateYOffset(lastBoundingBox, y);
        }

        lastBoundingBox = lastBoundingBox.offset(0.0D, y, 0.0D);

        boolean flag1 = data.isGround() || d4 != y && d4 < 0.0D;

        for (AxisAlignedBB bb : list1) {
            x = bb.calculateXOffset(lastBoundingBox, x);
        }

        lastBoundingBox = lastBoundingBox.offset(x, 0.0D, 0.0D);

        for (AxisAlignedBB bb : list1) {
            z = bb.calculateZOffset(lastBoundingBox, z);
        }

        lastBoundingBox = lastBoundingBox.offset(0.0D, 0.0D, z);

        // Post settings position

        if (d3 != x) {
            emulationData.addPostTask(() -> emulationData.setX(0));
        }

        if (d5 != z) {
            emulationData.addPostTask(() -> emulationData.setZ(0));
        }

        emulationData.setX(x);
        emulationData.setY(y);
        emulationData.setZ(z);

        return emulationData;
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(PlayerData playerData, BaseChunk chunk, AxisAlignedBB boundingBox) {
        List<AxisAlignedBB> boundingBoxes = new ArrayList<>();

        int minX = MathHelper.floor_double(boundingBox.minX);
        int minY = MathHelper.floor_double(boundingBox.minY);
        int minZ = MathHelper.floor_double(boundingBox.minZ);
        int maxX = MathHelper.floor_double(boundingBox.maxX + 1.0D);
        int maxY = MathHelper.floor_double(boundingBox.maxY + 1.0D);
        int maxZ = MathHelper.floor_double(boundingBox.maxZ + 1.0D);

        // TODO: account for world border
        // TODO: also this can be wrong when travelling between chunks, will fix soon

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    WrappedBlockState block = playerData.getWorldTracker().getBlock(x, y, z);

                    if (!block.getType().isSolid()) {
                        continue;
                    }

//                    List<AxisAlignedBB> blockBoundingBoxes = playerData.getWorldTracker().getBlock(playerData, block, x, y, z);

//                    boundingBoxes.addAll(blockBoundingBoxes);
//                    blockBoundingBoxes.stream().filter(boundingBox::intersectsWith).forEach(boundingBoxes::add);
                }
            }
        }

        // TODO: entities

        return boundingBoxes;
    }
}