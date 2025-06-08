package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.block.BlockUtil;
import integral.studios.hydro.util.collisions.WrappedBlock;
import integral.studios.hydro.util.math.MathHelper;
import integral.studios.hydro.util.mcp.AxisAlignedBB;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.mcp.PlayerUtil;
import integral.studios.hydro.util.packet.PacketUtil;
import lombok.Getter;
import org.bukkit.util.NumberConversions;

import java.util.List;

@Getter
public class CollisionTracker extends Tracker {

    // Only calculates half blocks like slabs and stairs when standing on the half,
    // For example if you are standing on the top of the stairs where youre one whole block
    // Above the bottom of the stair it wont count you as on stairs

    private boolean onSlime, lastOnSlime,
            onIce, lastOnIce,
            inWeb, lastInWeb,
            inWater, lastInWater, lastLastInWater,
            inLava, lastInLava, lastLastInLava,
            onClimbable, lastOnClimbable, lastLastOnClimbable,
            onStairs, lastOnStairs,
            onSlabs, lastOnSlabs,
            onSoulSand, lastOnSoulSand,
            bonking, lastBonking, lastLastBonking,
            nearWall, lastNearWall,
            nearPiston, lastNearPiston,
            clientGround, lastClientGround, lastLastClientGround,
            serverGround, lastServerGround;

    private float friction, lastFriction, lastLastFriction;

    private AxisAlignedBB boundingBox, lastBoundingBox, lastlastBoundingBox;

    private List<WrappedBlock> blocks;

    public CollisionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            actualize();

            clientGround = playerFlying.isOnGround();

            if (playerFlying.hasPositionChanged()) {
                friction = BlockUtil.getFriction(playerData.getWorldTracker().getBlock(
                        MathHelper.floor_double(playerData.getPositionTracker().getX()),
                        MathHelper.floor_double(playerData.getPositionTracker().getY()) - 1,
                        MathHelper.floor_double(playerData.getPositionTracker().getZ())));

                boundingBox = PlayerUtil.getBoundingBox(
                                playerData.getPositionTracker().getX(),
                                playerData.getPositionTracker().getY(),
                                playerData.getPositionTracker().getZ()).
                        expand(0.001, 0.001, 0.001);

                // the floored x,y,z values
                int floorX = NumberConversions.floor(playerFlying.getLocation().getX());
                int floorY = NumberConversions.floor(playerFlying.getLocation().getY());
                int floorZ = NumberConversions.floor(playerFlying.getLocation().getZ());

                int floorYHead = NumberConversions.floor(playerData.getPositionTracker().getY() + 1.8F);


                blocks = boundingBox.expand(0, 0, 1, 0, 0, 0).getBlocks(playerData);

                bonking = blocks.stream().anyMatch(block -> block.getY() >= floorYHead
                        && block.getCollisionBox().isCollided(boundingBox.expand(-0.001, 0, -0.001)));

                onSlime = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.SLIME_BLOCK
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);

                onSoulSand = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.SOUL_SAND
                        && block.getX() == floorX && block.getY() == floorY && block.getZ() == floorZ);

                onSlabs = blocks.stream().anyMatch(block -> BlockTags.SLABS.contains(block.getBlock().getType())
                        && (block.getY() == floorY || block.getY() == floorY - 1));

                onIce = blocks.stream().anyMatch(block -> BlockTags.ICE.contains(block.getBlock().getType())
                        && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);

                onStairs = blocks.stream().anyMatch(block -> BlockTags.STAIRS.contains(block.getBlock().getType())
                        && (block.getY() == floorY || block.getY() == floorY - 1));

                inWeb = blocks.stream().anyMatch(block -> block.getBlock().getType() == StateTypes.COBWEB
                        && block.getY() >= floorY && block.getY() <= floorYHead);


                nearWall = blocks.stream().anyMatch(block
                        -> block.getCollisionBox().isCollided(boundingBox.expand(0, -0.001, 0)));

                nearPiston = blocks.stream().anyMatch(block ->
                        block.getBlock().getType() == StateTypes.PISTON ||
                                block.getBlock().getType() == StateTypes.PISTON_HEAD ||
                                block.getBlock().getType() == StateTypes.MOVING_PISTON ||
                                block.getBlock().getType() == StateTypes.STICKY_PISTON) ||

                        blocks.stream().anyMatch(block
                                -> block.getBlock().getType() == StateTypes.PISTON ||
                                block.getBlock().getType() == StateTypes.PISTON_HEAD ||
                                block.getBlock().getType() == StateTypes.MOVING_PISTON ||
                                block.getBlock().getType() == StateTypes.STICKY_PISTON);

                onClimbable = BlockTags.CLIMBABLE.contains(playerData.getWorldTracker()
                        .getBlock(floorX, floorY, floorZ).getType());

                inWater = PlayerUtil.isInWater(playerData);

                inLava = PlayerUtil.isInLava(playerData);

                if (lastBoundingBox != null) {
                    // modulo isn't always accurate due to 0.03 but we use it to fix stepping having false
                    List<WrappedBlock> expandedBlocks = lastBoundingBox.clone().addCoord(
                                    playerData.getPositionTracker().getDeltaX(),
                                    playerData.getPositionTracker().getDeltaY(),
                                    playerData.getPositionTracker().getDeltaZ()
                            ).expand(0.001, 0.001, 0.001)
                            .getBlocks(playerData);

                    serverGround = playerData.getPositionTracker().getY() % 0.015625 == 0
                            && expandedBlocks.stream().anyMatch(block -> block.getY() <= floorY
                            && block.getBlock().getType().isSolid());
                } else {
                    serverGround = true;
                }
            }
        }
    }



    private void actualize() {
        lastOnSlime = onSlime;
        lastOnIce = onIce;
        lastInWeb = inWeb;
        lastLastInWater = lastInWater;
        lastInWater = inWater;
        lastLastInLava = lastInLava;
        lastInLava = inLava;
        lastLastOnClimbable = lastOnClimbable;
        lastOnClimbable = onClimbable;
        lastOnStairs = onStairs;
        lastOnSlabs = onSlabs;
        lastOnSoulSand = onSoulSand;
        lastLastBonking = lastBonking;
        lastBonking = bonking;
        lastNearWall = nearWall;
        lastNearPiston = nearPiston;
        lastLastFriction = lastFriction;
        lastFriction = friction;
        lastlastBoundingBox = lastBoundingBox;
        lastBoundingBox = boundingBox;
        lastLastClientGround = lastClientGround;
        lastClientGround = clientGround;
        lastServerGround = serverGround;
    }
}