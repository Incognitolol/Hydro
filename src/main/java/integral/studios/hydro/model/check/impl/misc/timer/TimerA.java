package integral.studios.hydro.model.check.impl.misc.timer;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.DetailedPlayerViolation;
import integral.studios.hydro.util.math.EvictingList;
import integral.studios.hydro.util.math.MathUtil;

public class TimerA extends PacketCheck {
    private final EvictingList<Long> samples = new EvictingList<>(50);

    private long lastFlying;

    public TimerA(PlayerData playerData) {
        super(playerData, "Timer A", "Modification Game Speed Check", "Incognito", new ViolationHandler(15, 120L), Category.MISC, SubCategory.TIMER);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            long delay = event.getTimestamp() - lastFlying;

            boolean exempt = Hydro.get().isLagging();

            add: {
                if (delay < 5 || exempt) {
                    break add;
                }

                samples.add(delay);
            }

            if (samples.isFull()) {
                double average = MathUtil.getAverage(samples);
                double speed = 50 / average;

                if (speed > 1.03) {
                    if (++vl > 40) {
                        // I wouldn't ban just kick the player bru
                        handleViolation(new DetailedPlayerViolation(this,
                                "\n- §3Speed: §b" + speed * 100 + "%"));
                    }
                } else {
                    decreaseVl(2.25);
                }
            }

            lastFlying = event.getTimestamp();
        } else if (teleportTracker.isTeleporting()) {
            samples.add(110L);
        }
    }
}