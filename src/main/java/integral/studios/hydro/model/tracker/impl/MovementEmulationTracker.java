package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.util.math.client.ClientMath;
import integral.studios.hydro.util.math.client.impl.OptifineMath;
import integral.studios.hydro.util.math.client.impl.VanillaMath;
import integral.studios.hydro.util.nms.MovementUtil;
import integral.studios.hydro.util.nms.NmsUtil;
import integral.studios.hydro.util.packet.PacketHelper;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityPlayer;

/*
 * This is purposefully broken just like some other important reach stuff.
 * Continue to fix this. Look at it closely.
 * - Mexify
 */
@Getter
public class MovementEmulationTracker extends Tracker {
    private static final boolean[] BOOLEANS = {true, false};

    private float slipperiness = 0.6F;

    public double lowestMatch;

    private final OptifineMath optifineMath = new OptifineMath();

    private final VanillaMath vanillaMath = new VanillaMath();

    public MovementEmulationTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (PacketHelper.isFlying(event.getPacketType())) {
            VelocityTracker velocityTracker = playerData.getVelocityTracker();
            MovementTracker movementTracker = playerData.getMovementTracker();

            EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData.getBukkitPlayer());

            if (movementTracker.getLastLocation() == null) {
                return;
            }

            lowestMatch = Double.MAX_VALUE;

            for (int forw = -1; forw < 2; forw++) {
                for (int stra = -1; stra < 2; stra++) {
                    for (boolean attacking : BOOLEANS) {
                        for (boolean frictionApplying : BOOLEANS) {
                            for (boolean velocity : BOOLEANS) {
                                for (boolean using : BOOLEANS) {
                                    for (boolean fastMath : BOOLEANS) {
                                        for (boolean lastGround : BOOLEANS) {
                                            for (boolean sprinting : BOOLEANS) {
                                                for (boolean sneaking : BOOLEANS) {
                                                    for (boolean jumping : BOOLEANS) {
                                                        for (boolean ground : BOOLEANS) {
                                                            float forward = forw, strafe = stra;

                                                            ClientMath clientMath = fastMath ? optifineMath : vanillaMath;

                                                            double bruteforceX = movementTracker.getLastDeltaX();
                                                            double bruteforceZ = movementTracker.getLastDeltaZ();

                                                            if (velocity && !velocityTracker.getPossibleVelocities().isEmpty()) {
                                                                bruteforceX = velocityTracker.peekVelocity().getX();
                                                                bruteforceZ = velocityTracker.peekVelocity().getZ();
                                                            }

                                                            float friction = 0.91F;

                                                            if (lastGround) {
                                                                friction *= slipperiness;
                                                            }

                                                            if (frictionApplying) {
                                                                bruteforceX *= friction;
                                                                bruteforceZ *= friction;
                                                            }

                                                            friction = 0.91F;

                                                            if (ground) {
                                                                friction *= NmsUtil.getBlockFriction(
                                                                        entityPlayer.world,
                                                                        movementTracker.getLastLocation().getX(),
                                                                        movementTracker.getLastLocation().getY() - 1,
                                                                        movementTracker.getLastLocation().getZ()
                                                                );
                                                            }

                                                            if (Math.abs(bruteforceX) < 0.005D) {
                                                                bruteforceX = 0.0D;
                                                            }

                                                            if (Math.abs(bruteforceZ) < 0.005D) {
                                                                bruteforceZ = 0.0D;
                                                            }

                                                            /*
                                                             * Skip impossible cases
                                                             */
                                                            if (sprinting && sneaking) {
                                                                continue;
                                                            }

                                                            if (jumping && !ground) {
                                                                continue;
                                                            }

                                                            if (sprinting && forw <= 0) {
                                                                continue;
                                                            }

                                                            if (attacking && (playerData.getActionTracker().getLastAttack() > 2)) {
                                                                continue;
                                                            }

                                                            if (sneaking) {
                                                                forward *= (float) 0.3D;
                                                                strafe *= (float) 0.3D;
                                                            }

                                                            if (using) {
                                                                forward *= 0.2D;
                                                                strafe *= 0.2D;
                                                            }

                                                            if (attacking) {
                                                                bruteforceX *= 0.6D;
                                                                bruteforceZ *= 0.6D;
                                                            }

                                                            if (jumping && sprinting) {
                                                                float f = playerData.getRotationTracker().getYaw() * 0.017453292F;

                                                                bruteforceX -= clientMath.sin(f) * 0.2F;
                                                                bruteforceZ += clientMath.cos(f) * 0.2F;
                                                            }

                                                            forward *= 0.98F;
                                                            strafe *= 0.98F;

                                                            float weird = 0.16277136F / (friction * friction * friction);
                                                            float moveSpeed = ground ? weird * getAiMoveSpeed(sprinting) : (sprinting ? 0.026F : 0.02F);

                                                            double[] moveFlying = MovementUtil.moveFlying(
                                                                    forward,
                                                                    strafe,
                                                                    moveSpeed,
                                                                    bruteforceX,
                                                                    bruteforceZ,
                                                                    playerData.getRotationTracker().getYaw(),
                                                                    clientMath
                                                            );

                                                            bruteforceX = moveFlying[0];
                                                            bruteforceZ = moveFlying[1];

                                                            double offsetX = movementTracker.getDeltaX() - bruteforceX;
                                                            double offsetZ = movementTracker.getDeltaZ() - bruteforceZ;

                                                            double offset = (offsetX * offsetX) + (offsetZ * offsetZ);

                                                            if (offset < lowestMatch) {
                                                                lowestMatch = offset;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

            slipperiness = NmsUtil.getBlockFriction(entityPlayer.world,
                    movementTracker.getLastX(),
                    movementTracker.getLastY() - 1,
                    movementTracker.getLastZ()
            );
        }
    }

    private float getAiMoveSpeed(boolean sprint) {
        AttributeTracker attributeTracker = playerData.getAttributeTracker();

        // 0.1F normally  :(
        float baseValue = attributeTracker.getWalkSpeed();

        if (sprint) {
            baseValue *= 1.3F;
        }

        int speed = attributeTracker.getSpeedBoost();
        int slow = attributeTracker.getSlowness();

        baseValue += baseValue * speed * 0.2F;
        baseValue += baseValue * slow * -0.15F;

        return baseValue;
    }
}