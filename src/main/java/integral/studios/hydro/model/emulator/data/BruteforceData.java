package integral.studios.hydro.model.emulator.data;

import integral.studios.hydro.util.math.client.ClientMath;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BruteforceData {
    private boolean sneaking, using, ground, jumping, sprinting, attacking;

    private float forward, strafe;

    private ClientMath clientMath;

    public BruteforceData copy() {
        return new BruteforceData(
                sneaking,
                using,
                ground,
                jumping,
                sprinting,
                attacking,
                forward,
                strafe,
                clientMath
        );
    }
}