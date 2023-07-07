package integral.studios.hydro.util.nms;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import integral.studios.hydro.util.math.MathHelper;
import integral.studios.hydro.util.math.client.ClientMath;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class MovementUtil {
    private static final UUID SPRINTING_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    public double getMovementSpeed(List<WrapperPlayServerUpdateAttributes.PropertyModifier> propertyModifier, float base) {
        AtomicReference<Float> value = new AtomicReference<>(base);

        for (WrapperPlayServerUpdateAttributes.PropertyModifier modifier : propertyModifier) {
            if (modifier.getUUID().equals(SPRINTING_MODIFIER_UUID)) {
                continue;
            }

            float amount = (float) modifier.getAmount();

            switch (modifier.getOperation()) {
                case ADDITION: {
                    value.updateAndGet(v -> v + amount);
                    break;
                }

                case MULTIPLY_BASE: {
                    value.updateAndGet(v -> v + amount * v);
                    break;
                }

                case MULTIPLY_TOTAL: {
                    value.updateAndGet(v -> v + v * amount);
                    break;
                }
            }
        }

        return value.get();
    }

    public double[] moveFlying(float forward, float strafe, float friction, double motionX, double motionZ, float yaw, ClientMath math) {
        float combined = strafe * strafe + forward * forward;

        if (combined >= 1.0E-4F) {
            combined = MathHelper.sqrt_float(combined);

            if (combined < 1.0F) {
                combined = 1.0F;
            }

            combined = friction / combined;

            strafe *= combined;
            forward *= combined;

            float sin = math.sin(yaw * (float) Math.PI / 180.0F);
            float cos = math.cos(yaw * (float) Math.PI / 180.0F);

            motionX += strafe * cos - forward * sin;
            motionZ += forward * cos + strafe * sin;
        }

        return new double[]{
                motionX,
                motionZ
        };
    }
}