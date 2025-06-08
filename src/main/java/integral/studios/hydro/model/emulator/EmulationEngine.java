package integral.studios.hydro.model.emulator;

import integral.studios.hydro.model.emulator.handler.impl.LivingUpdateHandler;
import integral.studios.hydro.model.emulator.handler.impl.MoveEntityHandler;
import integral.studios.hydro.model.emulator.handler.impl.MoveEntityWithHeadingHandler;
import integral.studios.hydro.model.emulator.handler.impl.MoveFlyingHandler;
import integral.studios.hydro.util.math.client.ClientMath;
import integral.studios.hydro.util.math.client.impl.OptifineMath;
import integral.studios.hydro.util.math.client.impl.VanillaMath;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.emulator.data.BruteforceData;
import integral.studios.hydro.model.emulator.data.EmulationData;
import integral.studios.hydro.model.emulator.handler.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class EmulationEngine {
    private final static ClientMath vanillaMath = new VanillaMath();
    private final static ClientMath optifineMath = new OptifineMath();

    private final PlayerData playerData;

    private final List<BruteforceData> bruteforceDatas = new ArrayList<>();

    private final List<EmulationData> predictions = new ArrayList<>();

    private final LivingUpdateHandler livingUpdateHandler = new LivingUpdateHandler();

    private final MoveEntityWithHeadingHandler moveEntityWithHeadingHandler = new MoveEntityWithHeadingHandler();

    private final MoveFlyingHandler moveFlyingHandler = new MoveFlyingHandler();

    private final MoveEntityHandler moveEntityHandler = new MoveEntityHandler();

    double lastMotionX, lastMotionY, lastMotionZ;

    public void process() {
        bruteforceDatas.clear();
        predictions.clear();

        provideBruteforceData();
    }

    private void runPredictions() {
        final double x = playerData.getPositionTracker().getLastDeltaX();
        final double y = playerData.getPositionTracker().getLastDeltaY();
        final double z = playerData.getPositionTracker().getLastDeltaZ();

        for (BruteforceData bruteforceData : bruteforceDatas) {
            predictions.add(new EmulationData(bruteforceData, x, y, z));
            predictions.add(new EmulationData(bruteforceData, lastMotionX, lastMotionY, lastMotionZ));
        }

        int delayedFlyingTicks = playerData.getPositionTracker().getDelayedFlyingTicks();

        double lowestOffset = 1337,
                max = playerData.getPositionTracker().isPossiblyZeroThree() ? (0.03D * delayedFlyingTicks) : 1E-8;
        EmulationData closestData = null;

        for (EmulationData emulationData : predictions) {
            emulationData = livingUpdateHandler.process(emulationData.getBruteforceData(), playerData, emulationData);

            lowestOffset = Math.min(lowestOffset, calculateOffset(emulationData));
            closestData = emulationData;

            if (lowestOffset < max) {
                break;
            }
        }

        if (lowestOffset > max) {
            double finalLowestOffset = lowestOffset;
//            Bukkit.getScheduler().runTask(Hydro.get(), () -> Bukkit.broadcastMessage("o=" + finalLowestOffset + " m=" + max));
        }

        if (closestData == null)
            return;

        closestData.runPostTasks();

        lastMotionX = closestData.getX();
        lastMotionY = closestData.getY();
        lastMotionZ = closestData.getZ();
    }

    private double calculateOffset(final EmulationData data) {
        final Vector prediction = new Vector(data.getX(), 0, data.getZ());

        final Vector movement = new Vector(
                playerData.getPositionTracker().getDeltaX(),
                0,
                playerData.getPositionTracker().getDeltaZ()
        );

        return prediction.distance(movement);
    }

    private void provideBruteforceData() {
        for (int f = -1; f < 2; f++) {
            for (int s = -1; s < 2; s++) {
                for (int sp = 0; sp < 2; sp++) {
                    for (int sn = 0; sn < 2; sn++) {
                        for (int jp = 0; jp < 2; jp++) {
                            for (int ui = 0; ui < 2; ui++) {
                                for (int hs = 0; hs < 2; hs++) {
                                    for (int fm = 0; fm < 2; fm++) {
                                        boolean sprint = sp == 0;
                                        boolean sneak = sn == 0;
                                        boolean jump = jp == 0;
                                        boolean using = ui == 0;
                                        boolean hitSlowdown = hs == 0;
                                        boolean fastMath = fm == 1;
                                        boolean ground = playerData.getCollisionTracker().isLastClientGround();

                                        if (f <= 0 && sprint && ground) {
                                            continue;
                                        }

                                        bruteforceDatas.add(new BruteforceData(
                                                sneak,
                                                using,
                                                ground,
                                                jump,
                                                sprint,
                                                hitSlowdown,
                                                f,
                                                s,
                                                fastMath ? optifineMath : vanillaMath
                                        ));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        runPredictions();
    }
}