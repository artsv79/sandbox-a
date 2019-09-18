package artsv.play.a;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StreamStatPreciseTest {

    @Test
    void testWithPrimeNumbers() {
        StreamStatPrecise tested = new StreamStatPrecise();
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
        assertEquals(3125, tested.getAvg());
    }

    @Test
    void testMin() {
        assertTrue(Double.isNaN(new StreamStatPrecise().getMin()));
    }

    @Test
    void testMax() {
        assertTrue(Double.isNaN(new StreamStatPrecise().getMax()));
    }

    @Test
    void testAvg() {
        assertTrue(Double.isNaN(new StreamStatPrecise().getAvg()));
    }

    @Test
    void testSimple() {
        StreamStatPrecise shtuka = new StreamStatPrecise();
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
    void testSeries() {
        StreamStatPrecise shtuka = new StreamStatPrecise();
        int N = 100_000;
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
        System.out.println(sum);
        double expectedAvg = sum / (double)N;
        assertEquals(N, shtuka.getCount());
        assertEquals(min, shtuka.getMin());
        assertEquals(max, shtuka.getMax());
        assertEquals(expectedAvg, shtuka.getAvg());
    }

    @Test
    void testHackWithMaxValueOfN() {
        StreamStatPrecise shtuka = new StreamStatPrecise();
        shtuka.putNext(1);
        try {
            Field n = shtuka.getClass().getDeclaredField("n");
            n.setAccessible(true);
            n.setLong(shtuka, Long.MAX_VALUE-1);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        shtuka.putNext(1);
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
        int N = 10_000_0000;
        StreamStatPrecise shtuka = new StreamStatPrecise();
        for (int i = 0; i <= N; i++) {
            double elem = i*(Double.MAX_VALUE/N);
            shtuka.putNext(elem);
        }
        assertNotEquals(Double.MAX_VALUE, shtuka.getAvg());
        assertEquals(Double.MAX_VALUE/2.0, shtuka.getAvg());
    }

    @Test
    void testOverflow2() {
        int N = 100_000_000;
        StreamStatPrecise shtuka = new StreamStatPrecise();
        for (int i = 0; i < N; i++) {
            shtuka.putNext(Double.MAX_VALUE - i);
            shtuka.putNext(-Double.MAX_VALUE + i);
        }
        assertEquals(0.0, shtuka.getAvg());
    }

    @Test
    void testBigPlusSmall() {
        int N = 100_000_000;
        StreamStatPrecise shtuka = new StreamStatPrecise();
        for (int i = 0; i < N; i++) {
            shtuka.putNext(Double.MAX_VALUE - i);
            shtuka.putNext(i);
        }
        assertNotEquals(Double.MAX_VALUE, shtuka.getAvg());
        assertEquals(Double.MAX_VALUE/2, shtuka.getAvg());
    }
}