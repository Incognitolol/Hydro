package integral.studios.hydro.model.check.type;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.packet.PacketHelper;

import java.util.LinkedList;
import java.util.Queue;

public abstract class ArmAnimationCheck extends PacketCheck {
    private final Queue<Integer> clickSamples = new LinkedList<>();

    private static final int MAX_COMBAT_TICKS = 20 * 60; // 1 minutecuh

    private final boolean combatCheck, doubleClicks;

    private final int maxClickSamples, maxMovements;

    private int movements;

    public ArmAnimationCheck(PlayerData playerData, String name, String desc, ViolationHandler violationHandler, boolean combatCheck, boolean doubleClicks, int maxSamples, int maxMovements) {
        super(playerData, name, desc, violationHandler, Category.COMBAT, SubCategory.AUTO_CLICKER);

        this.combatCheck = combatCheck;
        this.doubleClicks = doubleClicks;

        this.maxClickSamples = maxSamples;
        this.maxMovements = maxMovements;
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION && !actionTracker.isDigging()) {
            if (movements < maxMovements) {
                if (combatCheck && actionTracker.getLastAttack() > MAX_COMBAT_TICKS) {
                    return;
                }

                if (!doubleClicks && movements == 0) {
                    return;
                }

                if (clickSamples.add(movements) && clickSamples.size() == maxClickSamples) {
                    handle(clickSamples);

                    clickSamples.clear();
                }
            }

            movements = 0;
        } else if (PacketHelper.isFlying(event.getPacketType())) {
            ++movements;
        }
    }

    public abstract void handle(Queue<Integer> samples);
}