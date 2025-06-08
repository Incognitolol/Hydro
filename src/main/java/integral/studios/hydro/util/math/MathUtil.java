package integral.studios.hydro.util.math;

import integral.studios.hydro.util.mcp.Vec3;
import integral.studios.hydro.util.math.client.ClientMath;
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

    public float getRotationGcd(final float current, final float last) {
        final long currentExpanded = (long) (current * EXPANDER);
        final long lastExpanded = (long) (last * EXPANDER);

        return (float) (getGcd(currentExpanded, lastExpanded) / EXPANDER);
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

    public float angleDistanceNonAbsolute(float alpha, float beta) {
        float distance = beta - alpha;

        distance = (distance + 180) % 360 - 180;

        return distance;
    }

    public boolean isExponentiallySmall(final Number number) {
        return number.doubleValue() < 1 && Double.toString(number.doubleValue()).contains("E");
    }

    public boolean isExponentiallyLarge(final Number number) {
        return number.doubleValue() > 10000 && Double.toString(number.doubleValue()).contains("E");
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
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

    public static double getAverage2(Collection<? extends Number> numbers) {
        if (numbers.isEmpty()) {
            return 0D;
        }

        double sum = 0;

        for (Number num : numbers) {
            sum += num.doubleValue();
        }

        return numbers.isEmpty() ? 0 : sum / numbers.size();
    }

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }

    public static double getCPS(Collection<? extends Number> values) {
        return 20 / getAverage(values);
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
        if (a < remainder) {
            return (remainder - a);
        }

        return (div + remainder - a);
    }

    public static double[] dequeTranslator(Collection<? extends Number> samples) {
        return samples.stream().mapToDouble(Number::doubleValue).toArray();
    }

    public static double deviationSquared(Iterable<? extends Number> iterable) {
        double n = 0.0;

        int n2 = 0;

        for (Number anIterable : iterable) {
            n += (anIterable).doubleValue();

            ++n2;
        }

        double n3 = n / n2;
        double n4 = 0.0;

        for (Number anIterable : iterable) {
            n4 += FastMath.pow(anIterable.doubleValue() - n3, 2.0);
        }

        return (n4 == 0.0) ? 0.0 : (n4 / (n2 - 1));
    }

    public static double sqrt(double number) {
        if (number == 0) {
            return 0;
        }

        double t;
        double squareRoot = number / 2;

        // exploit fix
        if (Double.isInfinite(squareRoot)) {
            return 0;
        }

        do {
            t = squareRoot;
            squareRoot = (t + (number / t)) / 2;
        } while ((t - squareRoot) != 0);

        return squareRoot;
    }

    public static double getKurtosis(Collection<? extends Number> values) {
        double n = values.size();

        if (n < 3) {
            return Double.NaN;
        }

        double average = getAverage(values);
        double stDev = getStandardDeviation(values);

        double accum = 0;

        for (Number number : values) {
            accum += FastMath.pow(number.doubleValue() - average, 4D);
        }

        return n * (n + 1) / ((n - 1) * (n - 2) * (n - 3)) *
                (accum / FastMath.pow(stDev, 4D)) - 3 *
                FastMath.pow(n - 1, 2D) / ((n - 2) * (n - 3));
    }

    public static double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        double variance = 0;

        for (Number number : values) {
            variance += FastMath.pow(number.doubleValue() - average, 2D);
        }

        return sqrt(variance / values.size());
    }

    public static double getMedian(List<Double> data) {
        if (data.size() > 1) {
            if (data.size() % 2 == 0) {
                return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
            } else {
                return data.get(FastMath.round(data.size() / 2f));
            }
        }

        return 0;
    }

    public static double getMedian(Iterable<? extends Number> iterable) {
        List<Double> data = new ArrayList<>();

        for (Number number : iterable) {
            data.add(number.doubleValue());
        }

        return getMedian(data);
    }

    public static <T extends Number> T getMode(Collection<T> collect) {
        Map<T, Integer> repeated = new HashMap<>();

        //Sorting each value by how to repeat into a map.
        collect.forEach(val -> {
            int number = repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(tup -> tup.two, Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new).one;
    }

    public static double getSkewness(Iterable<? extends Number> iterable) {
        double sum = 0;

        int buffer = 0;

        List<Double> numberList = new ArrayList<>();

        for (Number num : iterable) {
            sum += num.doubleValue();
            buffer++;

            numberList.add(num.doubleValue());
        }

        Collections.sort(numberList);

        double mean = sum / buffer;
        double median = (buffer % 2 != 0) ? numberList.get(buffer / 2) : (numberList.get((buffer - 1) / 2) + numberList.get(buffer / 2)) / 2;

        return 3 * (mean - median) / deviationSquared(iterable);
    }

    public static double getVariance(Collection<? extends Number> samples) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        for (Number number : samples) {
            sum += number.doubleValue();
            ++count;
        }

        double average = sum / count;

        for (Number number : samples) {
            variance += number.doubleValue() - average * number.doubleValue() - average;
        }

        return variance / count;
    }

    public static Tuple<List<Double>, List<Double>> getOutliers(Collection<? extends Number> collection) {
        List<Double> values = new ArrayList<>();

        for (Number number : collection) {
            values.add(number.doubleValue());
        }

        if (values.size() < 4) {
            return new Tuple<>(new ArrayList<>(), new ArrayList<>());
        }

        double q1 = getMedian(values.subList(0, values.size() / 2)),
                q3 = getMedian(values.subList(values.size() / 2, values.size()));

        double iqr = Math.abs(q1 - q3);

        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        Tuple<List<Double>, List<Double>> tuple = new Tuple<>(new ArrayList<>(), new ArrayList<>());

        for (Double value : values) {
            if (value < lowThreshold) {
                tuple.one.add(value);
            } else if (value > highThreshold) {
                tuple.two.add(value);
            }
        }

        return tuple;
    }
}