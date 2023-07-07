package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.location.CustomLocation;
import integral.studios.hydro.util.math.MathUtil;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;

@Getter
public class MovementTracker extends Tracker {
    private CustomLocation customLocation;

    private CustomLocation lastLocation = new CustomLocation(0d, 0d, 0d, 0f, 0f, true);

    private CustomLocation lastLastLocation = new CustomLocation(0d, 0d, 0d, 0f, 0f, true);

    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX,
            lastDeltaY, lastDeltaZ, lastDeltaXZ, accelerationXZ;

    private double lastX, lastY, lastZ, x, y, z;

    private int ticksSincePosition, ticksSinceRotation;

    public MovementTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying playerFlying = new WrapperPlayClientPlayerFlying(event);
            Location location = playerFlying.getLocation();

            playerData.setTicksExisted(playerData.getTicksExisted() + 1);

            if (this.lastLocation != null) {
                this.lastLastLocation = this.lastLocation;
            }

            lastX = x;
            lastY = y;
            lastZ = z;

            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;
            lastDeltaXZ = deltaXZ;

            if (customLocation != null) {
                this.lastLocation = new CustomLocation(
                        lastX,
                        lastY,
                        lastZ,
                        customLocation.getYaw(),
                        customLocation.getPitch(),
                        customLocation.isOnGround()
                );
            }

            ticksSincePosition++;
            ticksSinceRotation++;

            if (playerFlying.hasPositionChanged()) {
                this.ticksSincePosition = 0;

                x = location.getX();
                y = location.getY();
                z = location.getZ();

                this.customLocation = new CustomLocation(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch(),
                        playerFlying.isOnGround()
                );

                deltaX = x - lastX;
                deltaY = y - lastY;
                deltaZ = z - lastZ;

                deltaXZ = MathUtil.hypot(deltaX, deltaZ);

                accelerationXZ = Math.abs(deltaXZ - lastDeltaXZ);

                if (!playerData.getTeleportTracker().isTeleporting()
                        && !playerData.getAttributeTracker().isCreativeMode()
                        && !playerData.getAttributeTracker().isFlightAllowed()
                        && !playerData.getAttributeTracker().isFlying()) {
                    playerData.getCheckData().getPositionChecks().forEach(check -> check.handle(customLocation, lastLocation));
                }
            }

            if (playerFlying.hasRotationChanged()) {
                this.ticksSinceRotation = 0;

                if (!playerData.getTeleportTracker().isTeleporting()
                        && !playerData.getAttributeTracker().isCreativeMode()
                        && !playerData.getAttributeTracker().isFlightAllowed()
                        && !playerData.getAttributeTracker().isFlying()) {
                    playerData.getCheckData().getRotationChecks().forEach(check -> check.handle(customLocation, lastLocation));
                }
            }
        }
    }
}