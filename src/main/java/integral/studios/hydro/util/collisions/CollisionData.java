package integral.studios.hydro.util.collisions;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.enums.Half;
import com.github.retrooper.packetevents.protocol.world.states.enums.Hinge;
import com.github.retrooper.packetevents.protocol.world.states.enums.Type;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.block.BlockUtil;
import integral.studios.hydro.util.pair.Pair;
import lombok.Getter;

import java.util.*;

@Getter
public enum CollisionData {

    DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1, 1), StateTypes.GRASS_BLOCK),
    AIR(new SimpleCollisionBox(0, 0, 0, 0, 0, 0), StateTypes.AIR),

    ANVIL((PlayerData player, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        BlockFace facing = block.getFacing();

        if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
            return new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            return new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
    }, BlockTags.ANVIL.getStates().toArray(new StateType[0])),

    PRESSURE_PLATE((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        if (block.isPowered()) {
            return new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
        }

        return new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
    }, BlockTags.PRESSURE_PLATES.getStates().toArray(new StateType[0])),

    BED(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F), BlockTags.BEDS.getStates().toArray(new StateType[0])),

    BREWING_STAND(new CompositeCollisionBox(
            new SimpleCollisionBox(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F),
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F)
    ), StateTypes.BREWING_STAND),

    CAKE((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        int bites = block.getBites();

        float f = (float) (1 + (bites * 2)) / 16.0F;

        return new SimpleCollisionBox(f, 0, 0.0625F, 1 - 0.0625F,
                data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)
                        ? 0.5F : 0.5F - 0.0625F, 1 - 0.0625F);
    }, StateTypes.CAKE),

    CARPET((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z)
            -> new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F,
            data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)
                    ? 1F / 16F : 0, 1.0F),
            BlockTags.WOOL_CARPETS.getStates().toArray(new StateType[0])),

    CAULDRON(new CompositeCollisionBox(
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F),
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F),
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F),
            new SimpleCollisionBox(1.0F - 0.125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
            new SimpleCollisionBox(0.0F, 0.0F, 1.0F - 0.125F, 1.0F, 1.0F, 1.0F)
    ), StateTypes.CAULDRON),

    CHEST((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {

        if (block.getTypeData() == Type.SINGLE)
            new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);

        WrappedBlockState northBlock = data.getWorldTracker().getBlock(x, y, z - 1);
        WrappedBlockState southBlock = data.getWorldTracker().getBlock(x, y, z + 1);
        WrappedBlockState eastBlock = data.getWorldTracker().getBlock(x + 1, y, z);
        WrappedBlockState westBlock = data.getWorldTracker().getBlock(x - 1, y, z);


        if (northBlock.getType() == block.getType()) {
            return new SimpleCollisionBox(0.0625F, 0F, 0F, 0.9375F, 0.875F, 0.937F);
        } else if (southBlock.getType() == block.getType()) {
            return new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (westBlock.getType() == block.getType()) {
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (eastBlock.getType() == block.getType()) {
            return new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        }

        return new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }, StateTypes.CHEST, StateTypes.TRAPPED_CHEST),

    COCOA((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        int age = block.getAge();

        int j = 4 + age * 2;
        int k = 5 + age * 2;
        float f = (float) j / 2.0F;

        switch (block.getFacing()) {
            case NORTH:
                return new SimpleCollisionBox((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float) j) / 16.0F);

            case SOUTH:
                return new SimpleCollisionBox((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, (15.0F - (float) j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);

            case EAST:
                return new SimpleCollisionBox((15.0F - (float) j) / 16.0F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);

            case WEST:
                return new SimpleCollisionBox(0.0625F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float) j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);

            default:
                return new SimpleCollisionBox(0, 0, 0, 0, 0, 0);
        }
    }, StateTypes.COCOA),

    DAYLIGHT_DETECTOR(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F),
            StateTypes.DAYLIGHT_DETECTOR),

    DOOR((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        float f = 0.1875F;

        Hinge hinge = Hinge.LEFT;
        BlockFace facing = BlockFace.EAST;
        boolean open = false;

        if (block.getHalf() == Half.UPPER) {
            WrappedBlockState below = data.getWorldTracker().getBlock(x, y - 1, z);

            hinge = block.getHinge();

            if (below.getType() == block.getType() && below.getHalf() == Half.LOWER) {
                facing = below.getFacing();
                open = below.isOpen();
            }
        } else {
            WrappedBlockState above = data.getWorldTracker().getBlock(x, y + 1, z);
            open = block.isOpen();
            facing = block.getFacing();

            if (above.getType() == block.getType() && above.getHalf() == Half.UPPER) {
                hinge = above.getHinge();
            }
        }

        boolean hingeLeft = hinge == Hinge.LEFT;

        if (open) {
            if (facing == BlockFace.EAST) {
                if (hingeLeft) {
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                } else {
                    return new SimpleCollisionBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            } else if (facing == BlockFace.SOUTH) {
                if (hingeLeft) {
                    return new SimpleCollisionBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            } else if (facing == BlockFace.WEST) {
                if (hingeLeft) {
                    return new SimpleCollisionBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                } else {
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            } else if (facing == BlockFace.NORTH) {
                if (hingeLeft) {
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                } else {
                    return new SimpleCollisionBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        } else if (facing == BlockFace.EAST) {
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        } else if (facing == BlockFace.SOUTH) {
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        } else if (facing == BlockFace.WEST) {
            return new SimpleCollisionBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else if (facing == BlockFace.NORTH) {
            return new SimpleCollisionBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

        // Should never happen
        return new SimpleCollisionBox(0, 0, 0, 0, 0, 0);
    }, BlockTags.DOORS.getStates().toArray(new StateType[0])),

    DRAGON_EGG(new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F), StateTypes.DRAGON_EGG),

    ENCHANTMENT_TABLE(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F), StateTypes.ENCHANTING_TABLE),

    ENDER_CHEST(new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F), StateTypes.ENDER_CHEST),

    ENDER_PORTAL_FRAME((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        CompositeCollisionBox box = new CompositeCollisionBox(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F));

        if (block.isEye()) {
            box.add(new SimpleCollisionBox(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F));
        }

        return box;
    }, StateTypes.END_PORTAL_FRAME),

    FARMLAND(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F), StateTypes.FARMLAND),

    FENCE((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        boolean north = BlockUtil.canConnectToFence(block, data.getWorldTracker().getBlock(x, y, z - 1));
        boolean south = BlockUtil.canConnectToFence(block, data.getWorldTracker().getBlock(x, y, z + 1));
        boolean east = BlockUtil.canConnectToFence(block, data.getWorldTracker().getBlock(x + 1, y, z));
        boolean west = BlockUtil.canConnectToFence(block, data.getWorldTracker().getBlock(x - 1, y, z));

        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.375F;
        float f3 = 0.625F;

        CompositeCollisionBox collisionBox = new CompositeCollisionBox();

        if (north) {
            f2 = 0.0F;
        }

        if (south) {
            f3 = 1.0F;
        }

        if (north || south) {
            collisionBox.add(new SimpleCollisionBox(f, 0.0F, f2, f1, 1.5F, f3));
        }

        f2 = 0.375F;
        f3 = 0.625F;

        if (west) {
            f = 0.0F;
        }

        if (east) {
            f1 = 1.0F;
        }

        if (west || east || !north && !south) {
            collisionBox.add(new SimpleCollisionBox(f, 0.0F, f2, f1, 1.5F, f3));
        }

        return collisionBox;
    }, BlockTags.FENCES.getStates().toArray(new StateType[0])),

    FLOWER_POT(new SimpleCollisionBox(0.5F - (0.375F / 2), 0.0F, 0.5F - (0.375F / 2), 0.5F + (0.375F / 2), 0.375F, 0.5F + (0.375F / 2)),
            BlockTags.FLOWER_POTS.getStates().toArray(new StateType[0])),

    HOPPER(new CompositeCollisionBox(
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F),
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F),
            new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F),
            new SimpleCollisionBox(1.0F - 0.125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
            new SimpleCollisionBox(0.0F, 0.0F, 1.0F - 0.125F, 1.0F, 1.0F, 1.0F)
    ), StateTypes.HOPPER),

    LADDER((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        switch (block.getFacing()) {
            case NORTH:
                return new SimpleCollisionBox(0.0F, 0.0F, 1.0F - 0.125F, 1.0F, 1.0F, 1.0F);

            case SOUTH:
                return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);

            case EAST:
            default:
                return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);

            case WEST:
                return new SimpleCollisionBox(1.0F - 0.125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }, StateTypes.LADDER),

    LILY_PAD(new SimpleCollisionBox(0F, 0F, 0F, 1F, 0.015625F, 1F),
            StateTypes.LILY_PAD),

    PANE((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        boolean north = BlockUtil.connectsToGlassPane(data.getWorldTracker().getBlock(x, y, z - 1));
        boolean south = BlockUtil.connectsToGlassPane(data.getWorldTracker().getBlock(x, y, z + 1));
        boolean east = BlockUtil.connectsToGlassPane(data.getWorldTracker().getBlock(x + 1, y, z));
        boolean west = BlockUtil.connectsToGlassPane(data.getWorldTracker().getBlock(x - 1, y, z));

        CompositeCollisionBox collisionBox = new CompositeCollisionBox();

        if ((!west || !east) && (west || east || north || south)) {
            if (west) {
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F));
            } else if (east) {
                collisionBox.add(new SimpleCollisionBox(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F));
            }
        } else {
            collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F));
        }

        if ((!north || !south) && (west || east || north || south)) {
            if (north) {
                collisionBox.add(new SimpleCollisionBox(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F));
            } else if (south) {
                collisionBox.add(new SimpleCollisionBox(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F));
            }
        } else {
            collisionBox.add(new SimpleCollisionBox(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F));
        }

        return collisionBox;
    }, BlockTags.GLASS_PANES.getStates().toArray(new StateType[0])),

    PISTON_BASE((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        if (!block.isExtended())
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1);

        switch (block.getFacing()) {
            default:
            case DOWN:
                return new SimpleCollisionBox(0F, 0.25F, 0F, 1F, 1F, 1F);

            case UP:
                return new SimpleCollisionBox(0F, 0F, 0F, 1F, 0.75F, 1F);

            case NORTH:
                return new SimpleCollisionBox(0F, 0F, 0.25F, 1F, 1F, 1F);

            case SOUTH:
                return new SimpleCollisionBox(0F, 0F, 0F, 1F, 1F, 0.75F);

            case WEST:
                return new SimpleCollisionBox(0.25F, 0F, 0F, 1F, 1F, 1F);

            case EAST:
                return new SimpleCollisionBox(0F, 0F, 0F, 0.75F, 1F, 1F);
        }
    }, StateTypes.PISTON, StateTypes.STICKY_PISTON),

    PISTON_HEAD((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        CompositeCollisionBox collisionBox = new CompositeCollisionBox();

        switch (block.getFacing()) {
            case DOWN:
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F));
                break;

            case UP:
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F));
                break;

            case NORTH:
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F));
                break;

            case SOUTH:
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F));
                break;

            case WEST:
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F));
                break;

            case EAST:
                collisionBox.add(new SimpleCollisionBox(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
        }

        switch (block.getFacing()) {
            case DOWN:
                collisionBox.add(new SimpleCollisionBox(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F));
                break;

            case UP:
                collisionBox.add(new SimpleCollisionBox(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F));
                break;

            case NORTH:
                collisionBox.add(new SimpleCollisionBox(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F));
                break;

            case SOUTH:
                collisionBox.add(new SimpleCollisionBox(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F));
                break;

            case WEST:
                collisionBox.add(new SimpleCollisionBox(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F));
                break;

            case EAST:
                collisionBox.add(new SimpleCollisionBox(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F));
        }

        return collisionBox;
    }, StateTypes.PISTON_HEAD),

    REDSTONE_DIODE(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F), StateTypes.REPEATER, StateTypes.COMPARATOR),

    SKULL(new SimpleCollisionBox(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F),
            StateTypes.CREEPER_HEAD, StateTypes.ZOMBIE_HEAD, StateTypes.DRAGON_HEAD, StateTypes.PLAYER_HEAD,
            StateTypes.SKELETON_SKULL, StateTypes.WITHER_SKELETON_SKULL),

    WALL_SKULL((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        switch (block.getFacing()) {
            case UP:
            default:
                return new SimpleCollisionBox(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);

            case NORTH:
                return new SimpleCollisionBox(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);

            case SOUTH:
                return new SimpleCollisionBox(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);

            case EAST:
                return new SimpleCollisionBox(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);

            case WEST:
                return new SimpleCollisionBox(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
        }
    }, StateTypes.CREEPER_WALL_HEAD, StateTypes.DRAGON_WALL_HEAD, StateTypes.PLAYER_WALL_HEAD, StateTypes.ZOMBIE_WALL_HEAD,
            StateTypes.SKELETON_WALL_SKULL, StateTypes.WITHER_SKELETON_WALL_SKULL),

    SLAB((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        Type typeData = block.getTypeData();

        if (typeData == Type.DOUBLE) {
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1);
        } else if (typeData == Type.BOTTOM) {
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        return new SimpleCollisionBox(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
    }, BlockTags.SLABS.getStates().toArray(new StateType[0])),

    SNOW((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z)
            -> new SimpleCollisionBox(0, 0, 0, 1, (float) block.getLayers() * 0.125F, 1), StateTypes.SNOW),


    STAIRS((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        CompositeCollisionBox collisionBox = new CompositeCollisionBox();

        Pair<SimpleCollisionBox, Boolean> pair = BlockUtil.func_176306_h(data, version, block, x, y, z);

        if (block.getHalf() == Half.TOP) {
            collisionBox.add(new SimpleCollisionBox(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F));
        } else {
            collisionBox.add(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F));
        }

        collisionBox.add(pair.getX());

        Pair<SimpleCollisionBox, Boolean> pair1 = BlockUtil.func_176304_i(data, version, block, x, y, z);

        if (pair.getY() && pair1.getY()) {
            collisionBox.add(pair1.getX());
        }

        return collisionBox;
    }, BlockTags.STAIRS.getStates().toArray(new StateType[0])),

    TRAPDOOR((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        if (block.isOpen()) {
            switch (block.getFacing()) {
                case NORTH:
                default:
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
                case SOUTH:
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
                case EAST:
                    return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
                case WEST:
                    return new SimpleCollisionBox(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        if (block.getTypeData() == Type.TOP) {
            return new SimpleCollisionBox(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        return new SimpleCollisionBox(0.0F, 0, 0.0F, 1.0F, 0.1875F, 1.0F);
    }, BlockTags.TRAPDOORS.getStates().toArray(new StateType[0])),

    WALL((PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) -> {
        boolean north = BlockUtil.canConnectToWall(data.getWorldTracker().getBlock(x, y, z - 1));
        boolean south = BlockUtil.canConnectToWall(data.getWorldTracker().getBlock(x, y, z + 1));
        boolean east = BlockUtil.canConnectToWall(data.getWorldTracker().getBlock(x + 1, y, z));
        boolean west = BlockUtil.canConnectToWall(data.getWorldTracker().getBlock(x - 1, y, z));
        float f = 0.25F;
        float f1 = 0.75F;
        float f2 = 0.25F;
        float f3 = 0.75F;
        float f4 = 1.0F;

        if (north) {
            f2 = 0.0F;
        }

        if (south) {
            f3 = 1.0F;
        }

        if (west) {
            f = 0.0F;
        }

        if (east) {
            f1 = 1.0F;
        }

        if (north && south && !west && !east) {
            f4 = 0.8125F;
            f = 0.3125F;
            f1 = 0.6875F;
        } else if (!north && !south && west && east) {
            f4 = 0.8125F;
            f2 = 0.3125F;
            f3 = 0.6875F;
        }

        return new SimpleCollisionBox(f, 0.0F, f2, f1, 1.5D, f3);
    }, BlockTags.WALLS.getStates().toArray(new StateType[0])),

    NONE(new SimpleCollisionBox(0, 0, 0, 0, 0, 0), StateTypes.AIR, StateTypes.LIGHT);

    private static final Map<StateType, CollisionData> rawLookupMap = new HashMap<>();

    static {
        for (CollisionData data : values()) {
            for (StateType type : data.materials) {
                rawLookupMap.put(type, data);
            }
        }
    }

    public final StateType[] materials;
    public CollisionBox box;
    public CollisionDataBuilder builder;

    CollisionData(CollisionBox box, StateType... states) {
        this.box = box;
        Set<StateType> mList = new HashSet<>(Arrays.asList(states));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new StateType[0]);
    }

    CollisionData(CollisionDataBuilder builder, StateType... states) {
        this.builder = builder;
        Set<StateType> mList = new HashSet<>(Arrays.asList(states));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new StateType[0]);
    }

    // Would pre-computing all states be worth the memory cost? I doubt it
    public static CollisionData getData(StateType state) { // TODO: Find a better hack for lava and scaffolding
        // What the fuck mojang, why put noCollision() and then give PITCHER_CROP collision?
        return state.isSolid() || state == StateTypes.LAVA || state == StateTypes.SCAFFOLDING || state == StateTypes.PITCHER_CROP || BlockTags.WALL_HANGING_SIGNS.contains(state) ? rawLookupMap.getOrDefault(state, DEFAULT) : NONE;
    }

    public static boolean contains(StateType state) {
        return rawLookupMap.containsKey(state);
    }

    public CollisionBox getMovementCollisionBoxes(PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
        if (this.box != null)
            return this.box.copy().offset(x, y, z);

        CollisionBox collisionBox = builder.fetch(data, version, block, x, y, z);

        return collisionBox.offset(x, y, z);
    }
}