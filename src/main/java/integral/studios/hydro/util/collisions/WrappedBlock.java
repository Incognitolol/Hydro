package integral.studios.hydro.util.collisions;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WrappedBlock {

    private final WrappedBlockState block;
    private final CollisionBox collisionBox;
    private final int x, y, z;
}
