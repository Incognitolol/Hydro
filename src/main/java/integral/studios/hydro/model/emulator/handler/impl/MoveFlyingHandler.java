package integral.studios.hydro.model.emulator.handler.impl;

import integral.studios.hydro.model.emulator.data.BruteforceData;
import integral.studios.hydro.model.emulator.data.EmulationData;
import integral.studios.hydro.model.emulator.handler.Handler;
import integral.studios.hydro.util.math.MathHelper;
import integral.studios.hydro.model.PlayerData;

public class MoveFlyingHandler implements Handler {
    @Override
    public EmulationData process(BruteforceData data, PlayerData playerData, EmulationData emulationData) {
        float strafe = data.getStrafe();
        float forward = data.getForward();

        float combined = strafe * strafe + forward * forward;

        EmulationData copyPred = emulationData.copy();

        if (combined >= 1.0E-4F) {
            combined = MathHelper.sqrt_float(combined);

            if (combined < 1.0F) {
                combined = 1.0F;
            }

            combined = emulationData.getMoveSpeed() / combined;

            strafe *= combined;
            forward *= combined;

            float yaw = playerData.getRotationTracker().getYaw();

            float sin = data.getClientMath().sin(yaw * (float) Math.PI / 180.0F);
            float cos = data.getClientMath().cos(yaw * (float) Math.PI / 180.0F);

            copyPred.addX(strafe * cos - forward * sin);
            copyPred.addZ(forward * cos + strafe * sin);
        }

        return copyPred;
    }
}