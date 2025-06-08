package integral.studios.hydro.util.math;

import integral.studios.hydro.util.math.fastmath.ApacheMath;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class StreamUtil {
    public static double getAverage(Collection<? extends Number> numbers) {
        if (numbers.isEmpty()) return 0D;
        double sum = 0;

        for (Number num : numbers) {
            sum += num.doubleValue();
        }

        return numbers.isEmpty() ? 0 : sum / numbers.size();
    }

    public static double getDeviation(final Collection<? extends Number> nums) {
        if (nums.isEmpty()) return 0D;

        return ApacheMath.sqrt((getVariance(nums) / (nums.size() - 1)));
    }

    public static double getVariance(final Collection<? extends Number> data) {
        if (data.isEmpty()) return 0D;

        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        double average;

        // Increase the sum and the count to find the average and the standard deviation
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        // Run the standard deviation formula
        for (final Number number : data) {
            variance += ApacheMath.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static int getDistinct(final Collection<? extends Number> collection) {
        if (collection.isEmpty()) return 0;

        return new HashSet<>(collection).size();
    }

    public static double getMaximumDouble(final List<Integer> nums) {
        if (nums.isEmpty()) return 0.0d;

        double max = Double.MIN_VALUE;

        for (final double val : nums) {
            if (val > max) max = val;
        }

        return max;
    }

    public static int getMaximumInt(final Collection<Integer> nums) {
        if (nums.isEmpty()) return 0;

        int max = Integer.MIN_VALUE;

        for (final int val : nums) {
            if (val > max) max = val;
        }

        return max;
    }

    public static long getMaximumLong(final Collection<Long> nums) {
        if (nums.isEmpty()) return 0L;

        long max = Long.MIN_VALUE;

        for (final long val : nums) {

            if (val > max) max = val;
        }

        return max;
    }

    public static float getMaximumFloat(final Collection<Float> nums) {
        if (nums.isEmpty()) return 0.0f;

        float max = Float.MIN_VALUE;

        for (final float val : nums) {

            if (val > max) max = val;
        }

        return max;
    }

    public static double getMinimumDouble(final List<Integer> nums) {
        if (nums.isEmpty()) return 0.0d;

        double min = Double.MAX_VALUE;

        for (final double val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static int getMinimumInt(final Collection<Integer> nums) {
        if (nums.isEmpty()) return 0;

        int min = Integer.MAX_VALUE;

        for (final int val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static long getMinimumLong(final Collection<Long> nums) {
        if (nums.isEmpty()) return 0L;

        long min = Long.MAX_VALUE;

        for (final long val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static float getMinimumFloat(final Collection<Float> nums) {
        if (nums.isEmpty()) return 0.0f;

        float min = Float.MAX_VALUE;

        for (final float val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static <T> boolean anyMatch(final List<T> objects, final Predicate<T> condition) {
        if (condition == null) return false;

        for (final T object : objects) {

            if (condition.test(object)) return true;
        }

        return false;
    }

    public static <T> boolean allMatch(final Collection<T> collection, final Predicate<T> condition) {
        if (condition == null) return false;

        for (final T object : collection) {

            if (!condition.test(object)) return false;
        }

        return true;
    }

    public static <T> List<T> getFiltered(final Collection<T> data, final Predicate<T> filter) {

        final List<T> list = new LinkedList<>();

        if (filter == null || data.isEmpty()) return list;

        for (final T object : data) {

            if (filter.test(object)) list.add(object);
        }

        return list;
    }

    public static <T> Collection<T> filter(Collection<T> data, Predicate<T> filter) {

        List<T> list = new LinkedList<>();

        if (filter == null || data.isEmpty()) return list;

        for (T object : data) {

            if (filter.test(object)) list.add(object);
        }

        return list;
    }

    public static <T> int filterNumberCount(Collection<T> data, Predicate<T> filter) {

        if (filter == null || data.isEmpty()) return 0;

        int loops = 0;

        for (T object : data) {
            loops++;
        }

        return loops;
    }

    public static int filteredCount(final Collection<? extends Number> data, final Predicate<Number> filter) {
        if (filter == null || data.isEmpty()) return 0;

        int count = 0;

        for (final Number num : data) {

            if (filter.test(num)) count++;
        }

        return count;
    }

    public static int getDuplicates(final Collection<?> data) {
        if (data.isEmpty()) return 0;

        return data.size() - distinct(data).size();
    }

    public static <T> Collection<T> distinct(final Collection<T> data) {
        return new HashSet<>(data);
    }
}
