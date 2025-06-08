package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.packet.PacketUtil;
import lombok.Getter;

@Getter
public class PositionTracker extends Tracker {
    public double x, lastX,
            y, lastY,
            z, lastZ;

    private double deltaX, lastDeltaX,
            deltaY, lastDeltaY,
            deltaZ, lastDeltaZ,
            deltaXZ, lastDeltaXZ,
            accelerationXZ;

    private boolean moved, lastMoved, lastLastMoved;

    private int delayedFlyingTicks;

    private boolean sentMotion, inLoadedChunk, lastInLoadedChunk;

    public PositionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            playerData.setTicksExisted(playerData.getTicksExisted() + 1);

            ++delayedFlyingTicks;

            lastLastMoved = lastMoved;
            lastMoved = moved;
            moved = playerFlying.hasPositionChanged();

            lastX = x;
            lastY = y;
            lastZ = z;

            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;

            lastDeltaXZ = deltaXZ;

            lastInLoadedChunk = inLoadedChunk;

            if (playerFlying.hasPositionChanged()) {
                x = playerFlying.getLocation().getX();
                y = playerFlying.getLocation().getY();
                z = playerFlying.getLocation().getZ();

                deltaX = x - lastX;
                deltaY = y - lastY;
                deltaZ = z - lastZ;

                deltaXZ = MathUtil.hypot(deltaX, deltaZ);

                accelerationXZ = Math.abs(deltaXZ - lastDeltaXZ);

                inLoadedChunk = playerData.getWorldTracker().isChunkLoaded(x, z);
                // Run in pos because 0.03 is impossible in unloaded chunk
                if (!inLoadedChunk) {
                    sentMotion = false;
                }

                // If they didn't send chunk motion we know for sure they arent in an unloaded chunk
                if (!playerData.getTeleportTracker().isTeleporting() && Math.abs(deltaY - (-0.1 * 0.9800000190734863D)) > 1E-10) {
                    sentMotion = true;
                }
            } else {
                // If the player didn't send position and they're above 0 + 0.03 leniency they have a loaded chunk
                if (y > 0.03) {
                    inLoadedChunk = sentMotion = true;
                }
            }
        }
    }

    @Override
    public void registerIncomingPostHandler(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);

            if (playerFlying.hasPositionChanged()) {
                delayedFlyingTicks = 0;
            }
        }
    }

    public boolean isPossiblyZeroThree() {
        return (!lastMoved || !lastLastMoved);
    }
}