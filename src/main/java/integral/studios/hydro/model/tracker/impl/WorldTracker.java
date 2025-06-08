package integral.studios.hydro.model.tracker.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.*;
import com.github.retrooper.packetevents.protocol.world.chunk.storage.LegacyFlexibleStorage;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.util.math.MathHelper;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Taken from Grim and other devs worked on it
@Getter
public class WorldTracker extends Tracker {

    private static final int CHUNK_HEIGHT = 16;
    private static final int CHUNK_SIZE = 16;
    private static final int COORD_MASK = 0xF;
    private static final int CHUNK_SHIFT = 4;

    private final Map<Long, BaseChunk[]> chunks = new ConcurrentHashMap<>();

    public WorldTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void registerOutgoingPreHandler(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            handleChunkData(event);
        } else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
            handleChunkBulk(event);
        } else if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            handleUnloadChunk(event);
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            handleBlockChange(event);
        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            handleMultiBlockChange(event);
        }
    }

    @Override
    public void registerIncomingPreHandler(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            handleBlockPlacement(event);
        }
    }

    private void handleChunkData(PacketSendEvent event) {
        WrapperPlayServerChunkData mapChunk = new WrapperPlayServerChunkData(event);

        playerData.getTransactionTracker().confirmPre(() -> {
            long chunkKey = getChunkKey(mapChunk.getColumn().getX(), mapChunk.getColumn().getZ());

            if (mapChunk.getColumn().isFullChunk()) {
                chunks.put(chunkKey, mapChunk.getColumn().getChunks());
            } else {
                updatePartialChunk(chunkKey, mapChunk.getColumn().getChunks());
            }
        });
    }

    private void handleChunkBulk(PacketSendEvent event) {
        WrapperPlayServerChunkDataBulk mapChunkBulk = new WrapperPlayServerChunkDataBulk(event);

        playerData.getTransactionTracker().confirmPre(() -> {
            int[] xCoords = mapChunkBulk.getX();
            int[] zCoords = mapChunkBulk.getZ();
            BaseChunk[][] chunkData = mapChunkBulk.getChunks();

            for (int i = 0; i < xCoords.length; i++) {
                long chunkKey = getChunkKey(xCoords[i], zCoords[i]);
                chunks.put(chunkKey, chunkData[i]);
            }
        });
    }

    private void handleUnloadChunk(PacketSendEvent event) {
        WrapperPlayServerUnloadChunk unloadChunk = new WrapperPlayServerUnloadChunk(event);
        long chunkKey = getChunkKey(unloadChunk.getChunkX(), unloadChunk.getChunkZ());

        playerData.getTransactionTracker().confirmPost(() -> chunks.remove(chunkKey));
    }

    private void handleBlockChange(PacketSendEvent event) {
        WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(event);
        Vector3i position = blockChange.getBlockPosition();

        playerData.getTransactionTracker().confirmPre(() -> {
            setBlock(position.getX(), position.getY(), position.getZ(), blockChange.getBlockId());
        });
    }

    private void handleMultiBlockChange(PacketSendEvent event) {
        WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(event);

        playerData.getTransactionTracker().confirmPre(() -> {
            for (WrapperPlayServerMultiBlockChange.EncodedBlock block : multiBlockChange.getBlocks()) {
                setBlock(block.getX(), block.getY(), block.getZ(), block.getBlockId());
            }
        });
    }

    private void handleBlockPlacement(PacketReceiveEvent event) {
        WrapperPlayClientPlayerBlockPlacement blockPlace = new WrapperPlayClientPlayerBlockPlacement(event);

        if (!blockPlace.getItemStack().isPresent()) {
            return;
        }

        var placedType = blockPlace.getItemStack().get().getType().getPlacedType();
        if (placedType == null) {
            return;
        }

        Vector3i placedPosition = getBlockPlacementPosition(blockPlace.getBlockPosition(), blockPlace.getFace());
        int blockId = placedType.createBlockState().getGlobalId();

        setBlock(placedPosition.getX(), placedPosition.getY(), placedPosition.getZ(), blockId);
    }

    private void updatePartialChunk(long chunkKey, BaseChunk[] newChunks) {
        BaseChunk[] existingChunks = chunks.get(chunkKey);
        if (existingChunks == null) {
            return;
        }

        for (int i = 0; i < Math.min(newChunks.length, CHUNK_HEIGHT); i++) {
            if (newChunks[i] != null) {
                existingChunks[i] = newChunks[i];
            }
        }
    }

    private void setBlock(int x, int y, int z, int blockId) {
        long chunkKey = getChunkKey(x >> CHUNK_SHIFT, z >> CHUNK_SHIFT);
        BaseChunk[] chunkColumn = chunks.computeIfAbsent(chunkKey, k -> new BaseChunk[CHUNK_HEIGHT]);

        int chunkY = y >> CHUNK_SHIFT;
        if (chunkY < 0 || chunkY >= chunkColumn.length) {
            return;
        }

        BaseChunk chunk = chunkColumn[chunkY];
        if (chunk == null) {
            chunk = createChunk();
            chunk.set(0, 0, 0, 0); // Initialize chunk
            chunkColumn[chunkY] = chunk;
        }

        chunk.set(x & COORD_MASK, y & COORD_MASK, z & COORD_MASK, blockId);
    }

    public WrappedBlockState getBlock(int x, int y, int z) {
        long chunkKey = getChunkKey(x >> CHUNK_SHIFT, z >> CHUNK_SHIFT);
        BaseChunk[] chunkColumn = chunks.get(chunkKey);

        if (chunkColumn == null) {
            return WrappedBlockState.getByGlobalId(0);
        }

        int chunkY = y >> CHUNK_SHIFT;
        if (chunkY < 0 || chunkY >= chunkColumn.length || chunkColumn[chunkY] == null) {
            return WrappedBlockState.getByGlobalId(0);
        }

        return chunkColumn[chunkY].get(
                playerData.getClientVersion(),
                x & COORD_MASK,
                y & COORD_MASK,
                z & COORD_MASK
        );
    }

    public WrappedBlockState getBlock(double x, double y, double z) {
        return getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public boolean isChunkLoaded(double x, double z) {
        long chunkKey = getChunkKey((int) x >> CHUNK_SHIFT, (int) z >> CHUNK_SHIFT);
        return chunks.containsKey(chunkKey) && chunks.get(chunkKey) != null;
    }


    public BaseChunk getChunk(double x, double y, double z) {
        long chunkKey = getChunkKey((int) x >> CHUNK_SHIFT, (int) z >> CHUNK_SHIFT);
        BaseChunk[] chunkColumn = chunks.get(chunkKey);

        if (chunkColumn == null) {
            return null;
        }

        int chunkY = (int) y >> CHUNK_SHIFT;
        if (chunkY < 0 || chunkY >= chunkColumn.length) {
            return null;
        }

        return chunkColumn[chunkY];
    }


    private static long getChunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    private static BaseChunk createChunk() {
        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        if (version.isNewerThanOrEquals(ServerVersion.V_1_18)) {
            return new Chunk_v1_18();
        } else if (version.isNewerThanOrEquals(ServerVersion.V_1_16)) {
            return new Chunk_v1_9(0, DataPalette.createForChunk());
        }

        // 1.8 and older versions
        return new Chunk_v1_9(0, new DataPalette(
                new ListPalette(4),
                new LegacyFlexibleStorage(4, 4096),
                PaletteType.CHUNK
        ));
    }

    private static Vector3i getBlockPlacementPosition(Vector3i clickedPos, BlockFace face) {
        return switch (face) {
            case UP -> clickedPos.add(0, 1, 0);
            case DOWN -> clickedPos.add(0, -1, 0);
            case NORTH -> clickedPos.add(0, 0, -1);
            case SOUTH -> clickedPos.add(0, 0, 1);
            case WEST -> clickedPos.add(-1, 0, 0);
            case EAST -> clickedPos.add(1, 0, 0);
            default -> clickedPos;
        };
    }

    public void clearChunks() {
        chunks.clear();
    }


    public int getLoadedChunkCount() {
        return chunks.size();
    }
}