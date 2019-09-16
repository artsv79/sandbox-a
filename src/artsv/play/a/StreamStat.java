package artsv.play.a;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class StreamStat {
    private long n;
    private double min, max, avg;
    private BigDecimal sum;
    public StreamStat() {
        n = 0;
        min = max = avg = Float.NaN;
        sum = new BigDecimal(0);
    }
    public void putNext(double x) {
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
                Double.isInfinite(max) ||
                Double.isNaN(avg)  ||
                Double.isInfinite(avg)) {
            min = max = avg = x;
        } else {
            min = Double.min(min, x);
            max = Double.max(max, x);
            avg = avg + (x-avg)/(double)n;
        }
        sum = sum.add(new BigDecimal(x));
    }
    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAvg1() {
        if (n > 0) {
            return sum.divide(new BigDecimal(n), RoundingMode.HALF_UP).doubleValue();
        } else {
            return Double.NaN;
        }
    }
    public double getAvg2() {
        return avg;
    }

    public long getCount() {
        return n;
    }
}
