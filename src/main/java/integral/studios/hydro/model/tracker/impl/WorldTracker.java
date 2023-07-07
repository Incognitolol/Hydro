package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.*;
import com.github.retrooper.packetevents.protocol.world.chunk.storage.LegacyFlexibleStorage;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.tracker.Tracker;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

//Credits to ScatDev https://github.com/ScatDev/Aquarium/blob/main/src/main/java/dev/scat/aquarium/data/processor/impl/WorldProcessor.java
//For the original code, for more information, visit https://github.com/ScatDev/Aquarium/blob/.
@Getter
public class WorldTracker extends Tracker {

    private final Map<Long, BaseChunk[]> chunks = new HashMap<>();

    public WorldTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData mapChunk = new WrapperPlayServerChunkData(event);

           playerData.getTransactionTracker().confirmPre(() -> {
                long xz = toLong(mapChunk.getColumn().getX(), mapChunk.getColumn().getZ());


                if (mapChunk.getColumn().isFullChunk()) {
                    chunks.put(xz, mapChunk.getColumn().getChunks());
                } else {
                    if (chunks.containsKey(xz)) {
                        BaseChunk[] currentChunks = chunks.get(xz);

                        for (int i = 0; i < 15; i++) {
                            BaseChunk chunk = mapChunk.getColumn().getChunks()[i];

                            if (chunk != null) {
                                currentChunks[i] = chunk;
                            }
                        }

                        chunks.put(xz, currentChunks);
                    }
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
            WrapperPlayServerChunkDataBulk mapChunkBulk = new WrapperPlayServerChunkDataBulk(event);

            playerData.getTransactionTracker().confirmPre(() -> {
                for (int i = 0; i < mapChunkBulk.getX().length; i++) {

                    int x = mapChunkBulk.getX()[i];
                    int z = mapChunkBulk.getZ()[i];

                    long xz = toLong(x, z);

                    chunks.put(xz, mapChunkBulk.getChunks()[i]);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            WrapperPlayServerUnloadChunk unloadChunk = new WrapperPlayServerUnloadChunk(event);

            long xz = toLong(unloadChunk.getChunkX(), unloadChunk.getChunkZ());

            playerData.getTransactionTracker().confirmPost(() -> {
                chunks.remove(xz);
            });
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(event);

            int x = blockChange.getBlockPosition().getX();
            int y = blockChange.getBlockPosition().getY();
            int z = blockChange.getBlockPosition().getZ();

            long xz = toLong(x >> 4, z >> 4);

            playerData.getTransactionTracker().confirmPre(() -> {
                if (!chunks.containsKey(xz)) {
                    chunks.put(xz, new BaseChunk[16]);
                }

                BaseChunk chunk = chunks.get(xz)[y >> 4];

                if (chunk == null) {
                    chunk = create();
                    chunk.set(0, 0, 0 ,0);
                    chunks.get(xz)[y >> 4] = chunk;
                }

                chunks.get(xz)[y >> 4].set(x & 0xF, y & 0xF, z & 0xF, blockChange.getBlockId());
            });
        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(event);

            for (WrapperPlayServerMultiBlockChange.EncodedBlock block : multiBlockChange.getBlocks()) {
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();

                long xz = toLong(x >> 4, z >> 4);

                playerData.getTransactionTracker().confirmPre(() -> {
                    if (!chunks.containsKey(xz)) {
                        chunks.put(xz, new BaseChunk[16]);
                    }

                    BaseChunk chunk = chunks.get(xz)[y >> 4];

                    if (chunk == null) {
                        chunk = create();
                        chunk.set(0, 0, 0 ,0);
                        chunks.get(xz)[y >> 4] = chunk;
                    }

                    chunks.get(xz)[y >> 4].set(x & 0xF, y & 0xF, z & 0xF, block.getBlockId());
                });
            }
        }
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement blockPlace = new WrapperPlayClientPlayerBlockPlacement(event);

            Vector3i pos = blockPlace.getBlockPosition();

            long xz = toLong(pos.getX() >> 4, pos.getZ() >> 4);

            boolean block = blockPlace.getItemStack().isPresent()
                    && blockPlace.getItemStack().get().getType().getPlacedType() != null;

            if (block && chunks.containsKey(xz)) {
                chunks.get(xz)[pos.getY() >> 4].set(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF,
                        blockPlace.getItemStack().get().getType().getId(playerData.getClientVersion()));
            }
        }
    }

    public long toLong(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }

    public WrappedBlockState getBlock(int x, int y, int z) {
        long xz = toLong(x >> 4, z >> 4);

        if (chunks.containsKey(xz)) {
            BaseChunk[] baseChunks = chunks.get(xz);

            if (y < 0 || (y >> 4) > baseChunks.length || baseChunks[y >> 4] == null)
                return WrappedBlockState.getByGlobalId(0);

            return baseChunks[y >> 4].get(x & 0xF, y & 0xF, z & 0xF);
        }
        return WrappedBlockState.getByGlobalId(0);
    }

    // Taken from grim cause im lazy tbh
    private static BaseChunk create() {
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
            return new Chunk_v1_18();
        } else if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16)) {
            return new Chunk_v1_9(0, DataPalette.createForChunk());
        }
        return new Chunk_v1_9(0, new DataPalette(new ListPalette(4), new LegacyFlexibleStorage(4, 4096), PaletteType.CHUNK));
    }
}
