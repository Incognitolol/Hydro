package integral.studios.hydro.util.math;

import integral.studios.hydro.util.math.client.ClientMath;
import integral.studios.hydro.util.mcp.Vec3;
import lombok.experimental.UtilityClass;
import org.apache.commons.math3.util.FastMath;

import java.util.*;

@UtilityClass
public class MathUtil {
    public final double EXPANDER = Math.pow(2, 24);

    public static Vec3 getVectorForRotation(float yaw, float pitch, ClientMath clientMath) {
        float f = clientMath.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = clientMath.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -clientMath.cos(-pitch * 0.017453292F);
        float f3 = clientMath.sin(-pitch * 0.017453292F);

        return new Vec3(f1 * f2, f3, f * f2);
    }

    public float getEyeHeight(boolean sneaking) {
        float eyeHeight = 1.62F;

        if (sneaking) {
            eyeHeight -= 0.08F;
        }

        return eyeHeight;
    }

    public static double hypot(double... value) {
        double total = 0;

        for (double val : value) {
            total += (val * val);
        }

        return FastMath.sqrt(total);
    }

    public static boolean isScientificNotation(final Float f) {
        return f.toString().contains("E");
    }

    public static float getDistanceBetweenAngles(final float angleOne, final float angleTwo) {
        float distance = Math.abs(angleOne - angleTwo) % 360.0f;
        if (distance > 180.0) {
            distance = 360.0f - distance;
        }
        return distance;
    }

    public boolean isExponentiallySmall(final Number number) {
        return number.doubleValue() < 1 && Double.toString(number.doubleValue()).contains("E");
    }

    public boolean isExponentiallyLarge(final Number number) {
        return number.doubleValue() > 10000 && Double.toString(number.doubleValue()).contains("E");
    }


    public double getGcd(final double a, final double b) {
        if (a < b) {
            return getGcd(b, a);
        }

        if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public static int getMode(Collection<? extends Number> array) {
        int mode = (int) array.toArray()[0];
        int maxCount = 0;

        for (Number value : array) {
            int count = 1;

            for (Number i : array) {
                if (i.equals(value)) {
                    ++count;
                }

                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }

        return mode;
    }

    public static double getAverage(Collection<? extends Number> samples) {
        double average = 0.0;

        double size = samples.size();

        for (Number number : samples) {
            average += number.doubleValue();
        }

        return average / size;
    }

    public static double getStandardDeviation(Collection<? extends Number> samples) {
        double average = 0.0;
        double std = 0.0;

        double size = samples.size();

        for (Number number : samples) {
            average += number.doubleValue();
        }

        double div = average / size;

        for (Number doubler : samples) {
            std += doubler.doubleValue() - div * doubler.doubleValue() - div;
        }

        return FastMath.sqrt(std / size);
    }

    public static double getEntropy(Collection<? extends Number> samples) {
        double n = samples.size();

        if (n < 3) {
            return Double.NaN;
        }

        Map<Double, Integer> valueCounts = new HashMap<>();

        for (Number value : samples) {
            valueCounts.put(value.doubleValue(), valueCounts.computeIfAbsent(value.doubleValue(), k -> 0) + 1);
        }

        double entropy = valueCounts.values().stream()
                .mapToDouble(freq -> (double) freq / n)
                .map(probability -> probability * (Math.log(probability) / Math.log(2D)))
                .sum();

        return -entropy;
    }

    public double getCps(Collection<? extends Number> samples) {
        return 20 / getAverage(samples);
    }

    public static double gcd(double a, double b) {
        if (a == 0) {
            return b;
        }

        if (Math.abs(a) >= Float.MAX_VALUE || Math.abs(b) >= Float.MAX_VALUE) {
            return 0;
        }

        if (a < b) {
            return gcd(b, a);
        } else if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return gcd(b, a - MathHelper.floor_double(a / b) * b);
        }
    }

    public double getReversedModulus(float div, float a, double remainder) {
        if (a < remainder)
            return (remainder - a);

        return (div + remainder - a);
    }

    public static double[] dequeTranslator(Collection<? extends Number> samples) {
        return samples.stream().mapToDouble(Number::doubleValue).toArray();
    }
}
