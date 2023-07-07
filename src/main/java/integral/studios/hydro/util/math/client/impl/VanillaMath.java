package integral.studios.hydro.util.math.client.impl;

import integral.studios.hydro.util.math.MathHelper;
import integral.studios.hydro.util.math.client.ClientMath;

public class VanillaMath implements ClientMath {
    @Override
    public float sin(float value) {
        return MathHelper.sin(value);
    }

    @Override
    public float cos(float value) {
        return MathHelper.cos(value);
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }
}