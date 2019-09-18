package artsv.play.a;

import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * Slow but precise implementation of Avarage calculator for stream of numbers (double).
 * Uses {@link BigDecimal} to accumulate sum of received sequence of numbers.
 * 25 times slower than {@link StreamStatFast} implementation.
 * Example:
 * <pre>
 * {@code
 *   StreamStatPrecise stat = new StreamStatPrecise();
 *   stat.putNext(1);
 *   stat.putNext(2);
 *   stat.putNext(3);
 *   double min = stat.getMin() // = 1
 *   double max = stat.getMax() // = 3
 *   double avarage = stat.getAvg() // = 2
 *   stat.putNext(4);
 *   stat.putNext(5);
 *   stat.putNext(6);
 *   double min = stat.getMin() // = 1
 *   double max = stat.getMax() // = 6
 *   double avarage = stat.getAvg() // = 3.5
 * }
 * </pre>
 */
public final class StreamStatPrecise {
    private long n;
    private double min, max;
    private BigDecimal sum;
    public StreamStatPrecise() {
        n = 0;
        min = max = Float.NaN;
        sum = new BigDecimal(0);
    }
    /**
     * The only processing method. Please, pass every sequence element as @param x, one at a time.<br>
     * {@link Double#NaN}, {@link Double#POSITIVE_INFINITY}, {@link Double#NEGATIVE_INFINITY} values will be ignored.<br>
     * <b>Thread-safe.</b>
     * @param x sequence element to process.
     *          Will affect {@link #getMin()}, {@link #getMax()}, {@link #getAvg()}.
     */
    public synchronized void putNext(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return; // silently ignore incorrect data
            //throw new ArithmeticException("NaN or INFINITE are not allowed");
        }

        if (n == Long.MAX_VALUE) { // against overflow.
            // one option - inore all incoming data.
            //return;

            // another option - throw an exception
            throw new ArithmeticException("StreamStat instance has reached sequence length limit of Long.MAX_VALUE");
        }
        if (n < 0) { // against setting 'n' to negative value via reflection
            n = 0;
        }
        n++; //293400 years of event stream of 1 000 000 events per 1 second

        // for the first step and against setting 'min/max/avg' to NaN via reflection
        if (n == 1  ||
                Double.isNaN(min) ||
                Double.isInfinite(min) ||
                Double.isNaN(max) ||
                Double.isInfinite(max)) {
            min = max = x;
        } else {
            min = Double.min(min, x);
            max = Double.max(max, x);
        }
        sum = sum.add(new BigDecimal(x));
    }
    /**
     * Returns min value of the sequence passed to {@link #putNext(double)}.
     * Returns {@link Double#NaN} if no numbers have been passed to {@link #putNext(double)} yet
     * @return min of the numbers in the received sequence
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns max value of the sequence passed to {@link #putNext(double)}.
     * Returns {@link Double#NaN} if no numbers have been passed to {@link #putNext(double)} yet
     * @return max of the numbers in the received sequence
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns avarage value of the sequence passed to {@link #putNext(double)}.
     * Returns {@link Double#NaN} if no numbers have been passed to {@link #putNext(double)} yet
     * @return avarage of the numbers in the received sequence
     */
    public double getAvg() {
        if (n > 0) {
            return sum.divide(new BigDecimal(n), 100, RoundingMode.HALF_UP).doubleValue();
        } else {
            return Double.NaN;
        }
    }
    /**
     * Returns number of sequence elements passed to {@link #putNext(double)}.
     * @return received sequence length
     */
    public long getCount() {
        return n;
    }
}
