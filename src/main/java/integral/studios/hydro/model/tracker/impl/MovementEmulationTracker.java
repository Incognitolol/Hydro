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
 * Fixed this for people to learn from, I cba to keep looking at this project and having it broken.
 */
@Getter
public class MovementEmulationTracker extends Tracker {

    // Array of boolean values used for iterating through different combinations
    private static final boolean[] BOOLEANS = {true, false};

    // Slipperiness factor used in calculations
    private float slipperiness = 0.6F;

    // Variable to store the lowest match offset
    public double lowestMatch;

    // Instances of OptifineMath and VanillaMath for calculations
    private final OptifineMath optifineMath = new OptifineMath();
    private final VanillaMath vanillaMath = new VanillaMath();

    // Constructor
    public MovementEmulationTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        // Check if the received packet is related to flying
        if (PacketHelper.isFlying(event.getPacketType())) {
            // Retrieve velocity and movement trackers
            VelocityTracker velocityTracker = playerData.getVelocityTracker();
            MovementTracker movementTracker = playerData.getMovementTracker();
            // Get the associated EntityPlayer object
            EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData.getBukkitPlayer());

            // If the last location is not available, return
            if (movementTracker.getLastLocation() == null) {
                return;
            }

            // Initialize the lowest match offset
            lowestMatch = Double.MAX_VALUE;

            // Loop through various combinations of parameters using nested loops
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
                                                            /*
                                                             * This section of code involves the calculation of movement parameters and simulation of movement scenarios.
                                                             */
                                                            float forward = forw, strafe = stra; // Initialize the movement directions based on the loop iteration

                                                            ClientMath clientMath = fastMath ? optifineMath : vanillaMath; // Select the math utility based on the value of 'fastMath'

                                                            double bruteforceX = movementTracker.getLastDeltaX(); // Initialize 'bruteforceX' with the player's last delta X
                                                            double bruteforceZ = movementTracker.getLastDeltaZ(); // Initialize 'bruteforceZ' with the player's last delta Z

                                                            // Update 'bruteforceX' and 'bruteforceZ' if velocity is enabled and there are possible velocities
                                                            if (velocity && !velocityTracker.getPossibleVelocities().isEmpty()) {
                                                                bruteforceX = velocityTracker.peekVelocity().getX();
                                                                bruteforceZ = velocityTracker.peekVelocity().getZ();
                                                            }

                                                            float friction = 0.91F; // Initialize the friction factor

                                                            // Apply extra friction if 'lastGround' is true
                                                            if (lastGround) {
                                                                friction *= slipperiness; // Reduce friction based on the slipperiness factor
                                                            }

                                                            // Apply friction to 'bruteforceX' and 'bruteforceZ' if 'frictionApplying' is true
                                                            if (frictionApplying) {
                                                                bruteforceX *= friction;
                                                                bruteforceZ *= friction;
                                                            }

                                                            friction = 0.91F; // Reset friction value

                                                            // Apply ground-specific friction using NmsUtil.getBlockFriction() method
                                                            if (ground) {
                                                                friction *= NmsUtil.getBlockFriction(
                                                                        entityPlayer.world,
                                                                        movementTracker.getLastLocation().getX(),
                                                                        movementTracker.getLastLocation().getY() - 1,
                                                                        movementTracker.getLastLocation().getZ()
                                                                );
                                                            }

                                                            // Make sure 'bruteforceX' and 'bruteforceZ' are not too close to zero
                                                            if (Math.abs(bruteforceX) < 0.005D) {
                                                                bruteforceX = 0.0D;
                                                            }
                                                            if (Math.abs(bruteforceZ) < 0.005D) {
                                                                bruteforceZ = 0.0D;
                                                            }

                                                            // Various conditions to skip impossible cases based on movement and action states
                                                            if (sprinting && sneaking) {
                                                                continue; // Skip iteration if both sprinting and sneaking
                                                            }
                                                            if (jumping && !ground) {
                                                                continue; // Skip iteration if jumping without being on the ground
                                                            }
                                                            if (sprinting && forw <= 0) {
                                                                continue; // Skip iteration if sprinting with non-positive forward value
                                                            }
                                                            if (attacking && (playerData.getActionTracker().getLastAttack() > 5)) {
                                                                continue; // Skip iteration if attacking and last attack is recent
                                                            }

                                                            // Apply modifiers to 'forward' and 'strafe' based on movement conditions
                                                            if (sneaking) {
                                                                forward *= (float) 0.3D; // Reduce forward and strafe if sneaking
                                                                strafe *= (float) 0.3D;
                                                            }
                                                            if (using) {
                                                                forward *= 0.2D; // Reduce forward and strafe if using an item
                                                                strafe *= 0.2D;
                                                            }
                                                            if (attacking) {
                                                                bruteforceX *= 0.6D; // Reduce 'bruteforceX' and 'bruteforceZ' if attacking
                                                                bruteforceZ *= 0.6D;
                                                            }

                                                            // Adjust 'bruteforceX' and 'bruteforceZ' based on jumping and sprinting conditions
                                                            if (jumping && sprinting) {
                                                                float f = playerData.getRotationTracker().getYaw() * 0.017453292F;

                                                                bruteforceX -= clientMath.sin(f) * 0.2F; // Adjust 'bruteforceX' based on yaw and jump
                                                                bruteforceZ += clientMath.cos(f) * 0.2F; // Adjust 'bruteforceZ' based on yaw and jump
                                                            }

                                                            // Apply a slight deceleration to 'forward' and 'strafe'
                                                            forward *= 0.98F;
                                                            strafe *= 0.98F;

                                                            // Calculate moveSpeed and weird factors based on conditions
                                                            float weird = 0.16277136F / (friction * friction * friction);
                                                            float moveSpeed = ground ? weird * getAiMoveSpeed(sprinting) : (sprinting ? 0.026F : 0.02F);

                                                            // Calculate new 'bruteforceX' and 'bruteforceZ' using MovementUtil.moveFlying() method
                                                            double[] moveFlying = MovementUtil.moveFlying(
                                                                    forward,
                                                                    strafe,
                                                                    moveSpeed,
                                                                    bruteforceX,
                                                                    bruteforceZ,
                                                                    playerData.getRotationTracker().getYaw(),
                                                                    clientMath
                                                            );

                                                            bruteforceX = moveFlying[0]; // Update 'bruteforceX'
                                                            bruteforceZ = moveFlying[1]; // Update 'bruteforceZ'

                                                            // Calculate offsets between calculated and actual delta X and Z
                                                            double offsetX = movementTracker.getDeltaX() - bruteforceX;
                                                            double offsetZ = movementTracker.getDeltaZ() - bruteforceZ;

                                                            // Calculate squared offset distance
                                                            double offset = (offsetX * offsetX) + (offsetZ * offsetZ);

                                                            // Update 'lowestMatch' if the current offset is lower
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

            // Calculate the slipperiness factor based on the block friction of the block below the player's location
            slipperiness = NmsUtil.getBlockFriction(entityPlayer.world,
                    movementTracker.getLastX(),
                    movementTracker.getLastY() - 1,
                    movementTracker.getLastZ()
            );
        }
    }

    // Calculate and return the player's AI move speed, considering sprinting and various modifiers
    private float getAiMoveSpeed(boolean sprint) {
        AttributeTracker attributeTracker = playerData.getAttributeTracker();

        // Retrieve the base walk speed value from the attribute tracker
        float baseValue = attributeTracker.getWalkSpeed();

        // Increase base walk speed if sprinting is enabled
        if (sprint) {
            baseValue *= 1.3F;
        }

        int speed = attributeTracker.getSpeedBoost(); // Get the speed boost attribute value
        int slow = attributeTracker.getSlowness();    // Get the slowness attribute value

        // Adjust the base walk speed based on speed boost and slowness attributes
        baseValue += baseValue * speed * 0.2F;
        baseValue += baseValue * slow * -0.15F;

        return baseValue; // Return the final adjusted AI move speed
    }
}