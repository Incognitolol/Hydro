package integral.studios.hydro.model.check.type;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.PlayerViolation;
import integral.studios.hydro.util.packet.PacketHelper;

import java.util.function.Predicate;

public abstract class PostActionCheck extends PacketCheck {
    private final Predicate<PacketReceiveEvent> predicate;

    private boolean sent = false;

    public long lastFlying, lastPacket;

    public PostActionCheck(PlayerData playerData, String name, String desc, Predicate<PacketReceiveEvent> predicate) {
        super(playerData, name, desc, "Mexify", new ViolationHandler(15, 30L), Category.COMBAT, SubCategory.POST);

        this.predicate = predicate;
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (teleportTracker.isTeleporting()) {
            sent = false;
            return;
        }

        if (PacketHelper.isFlying(event.getPacketType())) {
            long now = System.currentTimeMillis();
            long delay = now - lastPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    vl += 0.2;

                    if (++vl > 3) {
                        handleViolation(new PlayerViolation(this));
                    }
                } else {
                    decreaseVl(0.025D);
                }

                sent = false;
            }

            lastFlying = now;
        } else if (predicate.test(event)) {
            long now = System.currentTimeMillis();
            long delay = now - lastFlying;

            if (delay < 10L) {
                lastPacket = now;
                sent = true;
            } else {
                decreaseVl(0.2);
            }
        }
    }
}