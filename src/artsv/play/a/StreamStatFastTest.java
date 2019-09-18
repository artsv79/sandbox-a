package artsv.play.a;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

class StreamStatFastTest {

    @Test
    void testMin() {
        assertTrue(Double.isNaN(new StreamStatFast().getMin()));
    }

    @Test
    void testMax() {
        assertTrue(Double.isNaN(new StreamStatFast().getMax()));
    }

    @Test
    void testAvg() {
        assertTrue(Double.isNaN(new StreamStatFast().getAvg()));
    }

    @Test
    void testSimple() {
        StreamStatFast shtuka = new StreamStatFast();
        shtuka.putNext(0);
        assertEquals(0, shtuka.getMax());
        assertEquals(0, shtuka.getMin());
        assertEquals(0, shtuka.getAvg());
        shtuka.putNext(Double.NaN);
        assertEquals(0, shtuka.getMax());
        assertEquals(0, shtuka.getMin());
        assertEquals(0, shtuka.getAvg());
        shtuka.putNext(-10);
        assertEquals(0, shtuka.getMax());
        assertEquals(-10, shtuka.getMin());
        assertEquals(-5, shtuka.getAvg());
        assertEquals(2, shtuka.getCount());
    }

    @Test
    void testWithPrimeNumbers() {
        Random rnd = new Random();
        StreamStatFast tested = new StreamStatFast();
        double sum = 0;
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        int i;
        int count = 0;

        tested.putNext(700);
        tested.putNext(900);
        tested.putNext(1100);
        tested.putNext(1300);
        tested.putNext(1700);
        tested.putNext(1900);
        tested.putNext(2300);
        tested.putNext(2900);
        tested.putNext(3100);
        tested.putNext(3700);
        tested.putNext(4100);
        tested.putNext(4300);
        tested.putNext(4700);
        tested.putNext(5300);
        tested.putNext(5900);
        tested.putNext(6100);
        // true avarage is 3125.0 but getAvg() returns 3125.000000000001 due to precision limit of the method
        assertTrue(Math.abs(3125 - tested.getAvg()) <= 0.000000000001);
    }


    @Test
    void testSeries() {
        StreamStatFast shtuka = new StreamStatFast();
        int N = 100_000_000;
        double min = 0, max = 0;
        double sum = 0;
        Random rnd = new Random();
        for (int i = 0; i < N; i++) {
            double x = rnd.nextInt();
            shtuka.putNext(x);
            sum += x;
            if (i == 0) {
                min = max = x;
            } else {
                min = Double.min(min, x);
                max = Double.max(max, x);
            }
        }
        assertEquals(N, shtuka.getCount());
        assertEquals(min, shtuka.getMin());
        assertEquals(max, shtuka.getMax());

        double actualAvg = shtuka.getAvg();
        double expectedAvg = sum / (double)N;
        double actualRelativeError = Math.abs(expectedAvg-actualAvg)/Double.MAX_VALUE;
        assertTrue(actualRelativeError <= 1E-15, String.format("expected %e, got %e, relative error is %e", expectedAvg, actualAvg, actualRelativeError));
    }

    @Test
    void testHackWithMaxValueOfN() {
        StreamStatFast shtuka = new StreamStatFast();
        shtuka.putNext(1);
        try {
            Field n = shtuka.getClass().getDeclaredField("n");
            n.setAccessible(true);
            n.setLong(shtuka, Long.MAX_VALUE-1);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        shtuka.putNext(Long.MAX_VALUE);
        assertEquals(2, shtuka.getAvg());
        assertEquals(Long.MAX_VALUE, shtuka.getCount());


        try {
            shtuka.putNext(1); // Max N is reached.
            assertTrue(false); // PutNext should throw an exception
        } catch (ArithmeticException e) {
            // expected
        }
        assertEquals(Long.MAX_VALUE, shtuka.getCount());
    }

    @Test
    void testOverflow() {
        int N = 10_000_000;
        StreamStatFast shtuka = new StreamStatFast();
        for (int i = 0; i <= N; i++) {
            double elem = i*(Double.MAX_VALUE/N);
            shtuka.putNext(elem);
        }
        assertNotEquals(Double.MAX_VALUE, shtuka.getAvg());
        double actualAvg = shtuka.getAvg();
        double expectedAvg = Double.MAX_VALUE/2.0;
        double actualRelativeError = Math.abs(expectedAvg-actualAvg)/expectedAvg;
        // vary bad precision due to precision loss in manipulation of big numbers
        assertTrue(actualRelativeError <= 1E-6, String.format("expected %e, got %e, relative error is %e", expectedAvg, actualAvg, actualRelativeError));
    }

    @Test
    void testOverflow2() {
        int N = 100_000_000;
        StreamStatFast shtuka;
        shtuka = new StreamStatFast();
        for (int i = 0; i < N; i++) {
            shtuka.putNext(Double.MAX_VALUE);
            shtuka.putNext(-Double.MAX_VALUE);
        }
        assertEquals(Double.MAX_VALUE, shtuka.getMax());
        assertEquals(-Double.MAX_VALUE, shtuka.getMin());

        double actualAvg = shtuka.getAvg();
        double expectedAvg = 0;
        double actualRelativeError = Math.abs(expectedAvg-actualAvg)/Double.MAX_VALUE;
        assertTrue(actualRelativeError <= 1E-15, String.format("expected %e, got %e, relative error is %e", expectedAvg, actualAvg, actualRelativeError));
    }

    @Test
    void testBigPlusSmall() {
        int N = 100_000_000;
        StreamStatFast shtuka;
        shtuka = new StreamStatFast();
        for (int i = 0; i < N; i++) {
            shtuka.putNext(Double.MAX_VALUE);
            shtuka.putNext(i);
        }
        double actualAvg = shtuka.getAvg();
        double expectedAvg = Double.MAX_VALUE / 2;
        double actualRelativeError = Math.abs(expectedAvg-actualAvg)/expectedAvg;
        assertTrue(actualRelativeError <= 1E-15, String.format("expected %e, got %e, relative error is %e", expectedAvg, actualAvg, actualRelativeError));
    }
}