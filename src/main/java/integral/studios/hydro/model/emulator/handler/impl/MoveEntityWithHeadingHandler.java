package integral.studios.hydro.model.emulator.handler.impl;

import integral.studios.hydro.model.emulator.handler.Handler;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.emulator.data.BruteforceData;
import integral.studios.hydro.model.emulator.data.EmulationData;

public class MoveEntityWithHeadingHandler implements Handler {
    @Override
    public EmulationData process(BruteforceData data, PlayerData playerData, EmulationData emulationData) {
        double x = emulationData.getX();
        double y = emulationData.getY();
        double z = emulationData.getZ();

        EmulationData predictionCopy = emulationData.copy();

        float friction = 0.91F;

        if (data.isGround()) {
            friction *= playerData.getCollisionTracker().getLastFriction();
        }

        float moveSpeed = (float) playerData.getAttributeTracker().getMoveSpeed(data.isSprinting());
        float moveFlyingFriction;

        if (data.isGround()) {
            float moveSpeedMultiplier = 0.16277136F / (friction * friction * friction);

            moveFlyingFriction = moveSpeed * moveSpeedMultiplier;
        } else {
            moveFlyingFriction = (float) (data.isSprinting()
                    ? ((double) 0.02F + (double) 0.02F * 0.3D)
                    : 0.02F);
        }

        if (!data.isJumping() && !data.isGround()) {
            y -= 0.08;
            y *= 0.98F;
        }

        predictionCopy.setX(x);
        predictionCopy.setY(y);
        predictionCopy.setZ(z);

        predictionCopy.setMoveSpeed(moveFlyingFriction);

        MoveFlyingHandler moveFlyingHandler = playerData.getEmulationEngine().getMoveFlyingHandler();

        predictionCopy = moveFlyingHandler.process(data, playerData, predictionCopy);

        MoveEntityHandler moveEntityHandler = playerData.getEmulationEngine().getMoveEntityHandler();

        predictionCopy = moveEntityHandler.process(data, playerData, predictionCopy);

        return predictionCopy;
    }
}