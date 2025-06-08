package integral.studios.hydro.util.player;

import integral.studios.hydro.util.math.MathHelper;
import integral.studios.hydro.util.math.client.ClientMath;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MovementUtil {
    public float[] moveFlying(float moveForward, float moveStrafe, float friction, float rotationYaw, ClientMath clientMath) {
        float diagonal = moveStrafe * moveStrafe + moveForward * moveForward;

        float moveFlyingFactorX = 0.0F;
        float moveFlyingFactorZ = 0.0F;

        if (diagonal >= 1.0E-4F) {
            diagonal = MathHelper.sqrt_float(diagonal);

            if (diagonal < 1.0F) {
                diagonal = 1.0F;
            }

            diagonal = friction / diagonal;

            float strafe = moveStrafe * diagonal;
            float forward = moveForward * diagonal;

            float f1 = clientMath.sin(rotationYaw * (float) Math.PI / 180.0F);
            float f2 = clientMath.cos(rotationYaw * (float) Math.PI / 180.0F);

            float factorX = strafe * f2 - forward * f1;
            float factorZ = forward * f2 + strafe * f1;

            moveFlyingFactorX = factorX;
            moveFlyingFactorZ = factorZ;
        }

        return new float[]{
                moveFlyingFactorX,
                moveFlyingFactorZ
        };
    }
}