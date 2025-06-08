package integral.studios.hydro.util.block;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.enums.Half;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.collisions.CollisionBox;
import integral.studios.hydro.util.collisions.CollisionData;
import integral.studios.hydro.util.collisions.SimpleCollisionBox;
import integral.studios.hydro.util.pair.Pair;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
@UtilityClass
public class BlockUtil {

    public boolean connectsToGlassPane(WrappedBlockState target) {
        CollisionBox targetBox = CollisionData.getData(target.getType()).getBox();

        if (targetBox == null)
            return false;

        if (targetBox.isFullBlock())
            return true;

        if (BlockTags.GLASS_PANES.contains(target.getType()))
            return true;

        return BlockTags.GLASS_BLOCKS.contains(target.getType());
    }

    public Pair<SimpleCollisionBox, Boolean> func_176306_h(PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
        List<Pair<SimpleCollisionBox, Boolean>> possibleValues = new ArrayList<>();

        BlockFace facing = block.getFacing();
        Half half = block.getHalf();

        float f = 0.5F;
        float f1 = 1.0F;

        if (half == Half.TOP) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag1 = true;

        if (facing == BlockFace.EAST) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x + 1, y, z);

            f2 = 0.5F;
            f5 = 1.0F;

            if (BlockTags.STAIRS.contains(target.getType())
                    && target.getHalf() == block.getHalf()) {
                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.NORTH && !isSameStair(block, target)) {
                    f5 = 0.5F;
                    flag1 = false;
                } else if (targetFacing == BlockFace.SOUTH && !isSameStair(block, target)) {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (facing == BlockFace.WEST) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x - 1, y, z);

            f3 = 0.5F;
            f5 = 1.0F;

            if (BlockTags.STAIRS.contains(target.getType())) {
                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.NORTH && !isSameStair(block, target)) {
                    f5 = 0.5F;
                    flag1 = false;
                } else if (targetFacing == BlockFace.SOUTH && !isSameStair(block, target)) {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (facing == BlockFace.SOUTH) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x, y, z + 1);

            f4 = 0.5F;
            f5 = 1.0F;

            if (BlockTags.STAIRS.contains(target.getType()) && target.getHalf() == block.getHalf()) {
                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.WEST && !isSameStair(block, target)) {
                    f3 = 0.5F;
                    flag1 = false;
                } else if (targetFacing == BlockFace.EAST && !isSameStair(block, target)) {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (facing == BlockFace.NORTH) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x, y, z - 1);

            if (BlockTags.STAIRS.contains(target.getType()) && target.getHalf() == block.getHalf()) {
                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.WEST && !isSameStair(block, target)) {
                    f3 = 0.5F;
                    flag1 = false;
                } else if (targetFacing == BlockFace.EAST && !isSameStair(block, target)) {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }

        return new Pair<>(new SimpleCollisionBox(f2, f, f4, f3, f1, f5), flag1);
    }

    public Pair<SimpleCollisionBox, Boolean> func_176304_i(PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
        List<Pair<SimpleCollisionBox, Boolean>> possibleValues = new ArrayList<>();

        BlockFace facing = block.getFacing();

        float f = 0.5F;
        float f1 = 1.0F;

        if (block.getHalf() == Half.TOP) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.5F;
        float f5 = 1.0F;
        boolean flag1 = false;

        if (facing == BlockFace.EAST) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x + 1, y, z);

            if (BlockTags.STAIRS.contains(target.getType()) && target.getHalf() == block.getHalf()) {
                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.NORTH && !isSameStair(block, target)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                } else if (targetFacing == BlockFace.SOUTH && !isSameStair(block, target)) {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        } else if (facing == BlockFace.WEST) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x - 1, y, z);

            if (BlockTags.STAIRS.contains(target.getType()) && target.getHalf() == block.getHalf()) {
                f2 = 0.5F;
                f3 = 1.0F;

                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.NORTH && !isSameStair(block, target)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                } else if (targetFacing == BlockFace.SOUTH && !isSameStair(block, target)) {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }

        } else if (facing == BlockFace.SOUTH) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x, y, z + 1);

            if (BlockTags.STAIRS.contains(target.getType()) && target.getHalf() == block.getHalf()) {
                f4 = 0.0F;
                f5 = 0.5F;

                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.WEST && !isSameStair(block, target)) {
                    flag1 = true;
                } else if (targetFacing == BlockFace.EAST && !isSameStair(block, target)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        } else if (facing == BlockFace.NORTH) {
            WrappedBlockState target = data.getWorldTracker().getBlock(x, y, z - 1);

            if (BlockTags.STAIRS.contains(target.getType()) && target.getHalf() == block.getHalf()) {
                BlockFace targetFacing = target.getFacing();

                if (targetFacing == BlockFace.WEST && !isSameStair(block, target)) {
                    flag1 = true;
                } else if (targetFacing == BlockFace.EAST && !isSameStair(block, target)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        }

        if (flag1) {
            return new Pair<>(new SimpleCollisionBox(f2, f, f4, f3, f1, f5), flag1);
        }

        return new Pair<>(null, flag1);
    }

    public boolean isSameStair(WrappedBlockState block, WrappedBlockState target) {
        return BlockTags.STAIRS.contains(target.getType())
                && target.getHalf() == block.getHalf()
                && target.getFacing() == block.getFacing();
    }

    public boolean canConnectToFence(WrappedBlockState block, WrappedBlockState target) {
        if (target.getType() == StateTypes.BARRIER || target.getType() == StateTypes.AIR)
            return false;

        boolean netherBrickFence = block.getType() == StateTypes.NETHER_BRICK_FENCE;

        if ((netherBrickFence && target.getType() == StateTypes.NETHER_BRICK_FENCE)
                || (!netherBrickFence && BlockTags.WOODEN_FENCES.contains(target.getType())))
            return true;

        if (BlockTags.FENCE_GATES.contains(target.getType()))
            return true;

        if (target.getType() == StateTypes.MELON
                || target.getType() == StateTypes.PUMPKIN
                || target.getType() == StateTypes.CARVED_PUMPKIN
                || target.getType() == StateTypes.JACK_O_LANTERN)
            return false;

        CollisionBox targetBox = CollisionData.getData(target.getType()).getBox();

        if (targetBox == null)
            return false;

        if (target.getType().isBlocking() && targetBox.isFullBlock())
            return true;

        return false;
    }

    public boolean canConnectToWall(WrappedBlockState target) {
        if (target.getType() == StateTypes.BARRIER)
            return false;

        if (BlockTags.WALLS.contains(target.getType())
                || BlockTags.FENCE_GATES.contains(target.getType()))
            return true;

        if (target.getType() == StateTypes.MELON
                || target.getType() == StateTypes.PUMPKIN
                || target.getType() == StateTypes.CARVED_PUMPKIN
                || target.getType() == StateTypes.JACK_O_LANTERN)
            return false;

        CollisionBox targetBox = CollisionData.getData(target.getType()).getBox();

        if (targetBox == null)
            return false;

        return target.getType().isBlocking() && targetBox.isFullBlock();
    }

    public float getFriction(WrappedBlockState block) {
        if (BlockTags.ICE.contains(block.getType()))
            return 0.98F;

        if (block.getType() == StateTypes.SLIME_BLOCK)
            return 0.8F;

        return 0.6F;
    }
}
