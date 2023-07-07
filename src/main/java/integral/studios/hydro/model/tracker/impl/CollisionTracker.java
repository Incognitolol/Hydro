package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.location.WrappedBlock;
import integral.studios.hydro.util.mcp.BoundingBox;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.List;

@Getter
public class CollisionTracker extends Tracker {
    private boolean onGround, wasOnGround;

    private boolean underBlock, wasUnderBlock;

    private boolean collidedHorizontally, wasCollidedHorizontally;

    private boolean collidedVertically, wasCollidedVertically;

    private boolean onSlime, lastOnSlime,
            onIce, lastOnIce,
            inWater, lastInWater,
            inLava, lastInLava,
            onClimbable, lastOnClimbable,
            onStairs, lastOnStairs,
            onSlabs, lastOnSlabs,
            onSoulSand, lastOnSoulSand,
            inWeb, lastInWeb;

    public CollisionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            wasOnGround = onGround;
            wasCollidedHorizontally = collidedHorizontally;
            wasCollidedVertically = collidedVertically;
            wasUnderBlock = underBlock;

            lastOnSlime = onSlime;
            lastOnIce = onIce;
            lastInWeb = inWeb;
            lastInWater = inWater;
            lastInLava = inLava;
            lastOnClimbable = onClimbable;
            lastOnStairs = onStairs;
            lastOnSlabs = onSlabs;
            lastOnSoulSand = onSoulSand;

            if (playerData.getMovementTracker().getLastLocation() != null) {
                Player player = playerData.getBukkitPlayer();

                boolean flying = playerData.getAttributeTracker().isCreativeMode() || playerData.getAttributeTracker().isFlying() || playerData.getAttributeTracker().isFlightAllowed();

                if (playerData.getMovementTracker().getCustomLocation() == null) {
                    return;
                }

                if (playerFlying.hasPositionChanged()
                        && playerData.getMovementTracker().getCustomLocation().distanceSquared(playerData.getMovementTracker().getLastLocation()) > 0D
                        && !flying) {

                    // getting vertical and horizontal collisions boxes
                    BoundingBox horizontalBox = playerData.getMovementTracker().getCustomLocation().toCollisionBox();
                    BoundingBox verticalBox = playerData.getMovementTracker().getCustomLocation().toCollisionBox().expand(0.001, 0, 0.001);

                    // We need to get all colliding bounding boxes and then verify that they collide with the player

                    // get the all the blocks collided with the bounding box
                    List<WrappedBlock> horizontalBlocks = horizontalBox.getBlocks(playerData);
                    List<WrappedBlock> verticalBlocks = verticalBox.getBlocks(playerData);

                    double targetY = playerData.getMovementTracker().getCustomLocation().getY();

                    // the floored x,y,z values
                    int floorX = NumberConversions.floor(playerFlying.getLocation().getX());
                    int floorY = NumberConversions.floor(playerFlying.getLocation().getY());
                    int floorZ = NumberConversions.floor(playerFlying.getLocation().getZ());
                    int floorYHead = NumberConversions.floor(playerData.getMovementTracker().getCustomLocation().getY() + 1.8F);

                    wasCollidedHorizontally = collidedHorizontally;

                    onGround = playerFlying.getLocation().getY() % (1d / 64d) < 0.001;

                    onSlime = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.SLIME_BLOCK
                            && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);

                    onSoulSand = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.SOUL_SAND
                            && block.getX() == floorX && block.getY() == floorY && block.getZ() == floorZ);
                    onSlabs = verticalBlocks.stream().anyMatch(block -> BlockTags.SLABS.contains(block.getType())
                            && block.getY() == floorY);
                    onIce = verticalBlocks.stream().anyMatch(block -> BlockTags.ICE.contains(block.getType())
                            && block.getX() == floorX && block.getY() == floorY - 1 && block.getZ() == floorZ);
                    onStairs = verticalBlocks.stream().anyMatch(block -> BlockTags.SLABS.contains(block.getType())
                            && block.getY() == floorY);
                    inWeb = verticalBlocks.stream().anyMatch(block -> block.getType() == StateTypes.COBWEB
                            && block.getY() >= floorY && block.getY() <= floorYHead);

                    // underBlock = BBUtil.isCollidedAbove(bbs, targetY + 1.8);

                    collidedVertically = onGround || underBlock;
                }
            }
        }
    }
}