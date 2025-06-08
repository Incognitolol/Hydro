package integral.studios.hydro.util.task;

import org.bukkit.scheduler.BukkitRunnable;

public class ServerTickTask extends BukkitRunnable {
    private final static long MAX_ALLOWED_TICK_TIME = 120L;

    private int ticks;

    private long lastTickTime;

    @Override
    public void run() {
        ticks++;

        lastTickTime = System.currentTimeMillis();
    }

    /**
     * @return If the server is lagging
     */
    public boolean isLagging() {
        return System.currentTimeMillis() - lastTickTime > MAX_ALLOWED_TICK_TIME;
    }

    public int getTicks() {
        return ticks;
    }
}