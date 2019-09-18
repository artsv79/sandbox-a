package artsv.play.a;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Fast but not precise implementation of Avarage calculator for stream of numbers (double)
 * Example:
 * <pre>
 * {@code
 *   StreamStatFast stat = new StreamStatFast();
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
public final class StreamStatFast {
    private long n;
    private double min, max, avg;
    public StreamStatFast() {
        n = 0;
        min = max = avg = Float.NaN;
    }

    /**
     * The only processing method. Please, pass every sequence element as @param x, one at a time.<br>
     * {@link Double#NaN}, {@link Double#POSITIVE_INFINITY}, {@link Double#NEGATIVE_INFINITY} values will be ignored.<br>
     * <b>Not thread-safe.</b>
     * @param x sequence element to process.
     *          Will affect {@link #getMin()}, {@link #getMax()}, {@link #getAvg()}.
     */
    public void putNext(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return; // silently ignore incorrect data

            // another option - throw an exception:
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
                Double.isInfinite(max) ||
                Double.isNaN(avg)  ||
                Double.isInfinite(avg)) {
            min = max = avg = x;
        } else {
            min = Double.min(min, x);
            max = Double.max(max, x);

            // AVG(n+1) = ((AVG(n) * n) + x) / (n+1)  = AVG(n) + (x - AVG(n))/(n+1)
            // this formula allow not to accumulate huge sum in case of very long sequence.
            // Drawback of this approach is loss of precision on long sequences.
            if (Math.abs(x) > Double.MAX_VALUE / 2 || Math.abs(avg) > Double.MAX_VALUE / 2) {
                double v1 = x / (double) n;
                double v2 = avg / (double) n;
                double dv = v1 - v2;
                avg = avg + dv;
            } else {
                avg = avg + (x-avg)/(double)n;
            }
        }
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
        return avg;
    }

    /**
     * Returns number of sequence elements passed to {@link #putNext(double)}.
     * @return received sequence length
     */
    public long getCount() {
        return n;
    }
}
