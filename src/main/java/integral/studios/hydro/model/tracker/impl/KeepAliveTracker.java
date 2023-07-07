package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import it.unimi.dsi.fastutil.longs.Long2LongArrayMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import lombok.Getter;

@Getter
public class KeepAliveTracker extends Tracker {
    private final Long2LongMap expectedKeepAlives = new Long2LongArrayMap();

    private boolean acceptedKeepAlive;

    private long keepAlivePing;

    public KeepAliveTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive keepAlive = new WrapperPlayClientKeepAlive(event);

            long key = keepAlive.getId();
            long timestamp = event.getTimestamp();

            if (expectedKeepAlives.containsKey(key)) {
                keepAlivePing = timestamp - expectedKeepAlives.remove(key);
                acceptedKeepAlive = true;
            }
        }
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive serverKeepAlive = new WrapperPlayServerKeepAlive(event);

            expectedKeepAlives.put(serverKeepAlive.getId(), event.getTimestamp());
        }
    }
}