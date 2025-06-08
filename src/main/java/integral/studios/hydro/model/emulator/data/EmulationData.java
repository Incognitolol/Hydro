package integral.studios.hydro.model.emulator.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmulationData {
    private BruteforceData bruteforceData;

    private double x, y, z;

    private float moveSpeed;

    private List<Runnable> postTasks = new ArrayList<>();

    public EmulationData(BruteforceData bruteforceData, double x, double y, double z) {
        this.bruteforceData = bruteforceData;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void addX(final double v) {
        x += v;
    }

    public void addZ(final double v) {
        z += v;
    }

    public EmulationData copy() {
        return new EmulationData(bruteforceData, x, y, z);
    }

    public void addPostTask(Runnable runnable) {
        postTasks.add(runnable);
    }

    public void runPostTasks() {
        postTasks.forEach(Runnable::run);
    }
}