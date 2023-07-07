package integral.studios.hydro.model.tracker.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.thomazz.pledge.api.PacketFrame;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import dev.thomazz.pledge.api.event.ReceiveType;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import lombok.Getter;


@Getter
public class TransactionTracker extends Tracker {
    private final Multimap<Short, Runnable> scheduledTransactions = ArrayListMultimap.create();

    public TransactionTracker(PlayerData playerData) {
        super(playerData);
    }

    public void handleTransaction(PacketFrameReceiveEvent event) {
        short id = (short) (event.getType() == ReceiveType.RECEIVE_START
                ? event.getFrame().getId1()
                : event.getFrame().getId2()
        );

        if (scheduledTransactions.containsKey(id)) {
            for (Runnable runnable : scheduledTransactions.removeAll(id)) {
                runnable.run();
            }
        }
    }

    public void confirmPre(Runnable runnable) {
        PacketFrame frame = Hydro.get().getPledge().getOrCreateFrame(playerData.getBukkitPlayer());

        scheduledTransactions.put((short) frame.getId1(), runnable);
    }

    public void confirmPost(Runnable runnable) {
        PacketFrame frame = Hydro.get().getPledge().getOrCreateFrame(playerData.getBukkitPlayer());

        scheduledTransactions.put((short) frame.getId2(), runnable);
    }
}