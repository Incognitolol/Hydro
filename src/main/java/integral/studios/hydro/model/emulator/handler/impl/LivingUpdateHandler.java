package integral.studios.hydro.model.emulator.handler.impl;

import integral.studios.hydro.model.emulator.data.BruteforceData;
import integral.studios.hydro.model.emulator.data.EmulationData;
import integral.studios.hydro.model.emulator.handler.Handler;
import integral.studios.hydro.model.PlayerData;

public class LivingUpdateHandler implements Handler {
    @Override
    public EmulationData process(BruteforceData data, PlayerData playerData, EmulationData emulationData) {
        double x = emulationData.getX();
        double y = emulationData.getY();
        double z = emulationData.getZ();

        if (playerData.getCollisionTracker().isLastClientGround()) {
            x *= (playerData.getCollisionTracker().getLastLastFriction() * 0.91F);
            z *= (playerData.getCollisionTracker().getLastLastFriction() * 0.91F);
        } else {
            x *= 0.91F;
            z *= 0.91F;
        }

        BruteforceData copyData = data.copy();

        EmulationData predictionCopy = emulationData.copy();

        float moveForward = data.getForward();
        float moveStrafe = data.getStrafe();

        if (data.isSneaking()) {
            moveForward *= (float) 0.3D;
            moveStrafe *= (float) 0.3D;
        }

        if (data.isUsing()) {
            moveForward *= 0.2F;
            moveStrafe *= 0.2F;
        }

        moveForward *= 0.98F;
        moveStrafe *= 0.98F;

        if (data.isAttacking()) {
            x *= 0.6D;
            z *= 0.6D;
        }

        if (Math.abs(x) < 0.005D) {
            x = 0.0D;
        }

        if (Math.abs(y) < 0.005D) {
            y = 0.0D;
        }

        if (Math.abs(z) < 0.005D) {
            z = 0.0D;
        }

        if (data.isJumping()) {
            y = 0.42F + (playerData.getAttributeTracker().getJumpBoost() * 0.1F);

            if (data.isSprinting()) {
                float f = playerData.getRotationTracker().getYaw() * 0.017453292F;

                x -= data.getClientMath().sin(f) * 0.2F;
                z += data.getClientMath().cos(f) * 0.2F;
            }
        }

        copyData.setForward(moveForward);
        copyData.setStrafe(moveStrafe);

        MoveEntityWithHeadingHandler moveEntityWithHeading = playerData.getEmulationEngine().getMoveEntityWithHeadingHandler();

        predictionCopy.setX(x);
        predictionCopy.setY(y);
        predictionCopy.setZ(z);
        predictionCopy.setBruteforceData(copyData);

        predictionCopy = moveEntityWithHeading.process(copyData, playerData, predictionCopy);

        return predictionCopy;
    }
}