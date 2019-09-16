package artsv.play.a;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

class StreamStatTest {

    @Test
    void testWithPrimeNumbers() {
        Random rnd = new Random();
        StreamStat tested = new StreamStat();
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
        assertEquals(3125, tested.getAvg1());
    }

    @Test
    void testMin() {
        assertTrue(Double.isNaN(new StreamStat().getMin()));
    }

    @Test
    void testMax() {
        assertTrue(Double.isNaN(new StreamStat().getMax()));
    }

    @Test
    void testAvg() {
        assertTrue(Double.isNaN(new StreamStat().getAvg1()));
    }

    @Test
    void testSimple() {
        StreamStat shtuka = new StreamStat();
        shtuka.putNext(0);
        assertEquals(0, shtuka.getMax());
        assertEquals(0, shtuka.getMin());
        assertEquals(0, shtuka.getAvg1());
        shtuka.putNext(Double.NaN);
        assertEquals(0, shtuka.getMax());
        assertEquals(0, shtuka.getMin());
        assertEquals(0, shtuka.getAvg1());
        shtuka.putNext(-10);
        assertEquals(0, shtuka.getMax());
        assertEquals(-10, shtuka.getMin());
        assertEquals(-5, shtuka.getAvg1());
        assertEquals(2, shtuka.getCount());
    }

    @Test
    void testSeries() {
        StreamStat shtuka = new StreamStat();
        int N = 100_000_000;
        double min = 0, max = 0;
        double sum = 0;
//        BigDecimal bSum = new BigDecimal(0);
        Random rnd = new Random();
        for (int i = 0; i < N; i++) {
            double x = rnd.nextInt();
            shtuka.putNext(x);
            sum += x;
//            bSum = bSum.add(new BigDecimal(x));
            if (i == 0) {
                min = max = x;
            } else {
                min = Double.min(min, x);
                max = Double.max(max, x);
            }
        }
        System.out.println(sum);
        double avg = sum / (double)N;
        assertEquals(N, shtuka.getCount());
        assertEquals(min, shtuka.getMin());
        assertEquals(max, shtuka.getMax());
        assertEquals(avg, shtuka.getAvg1());
    }

    @Test
    void testHackWithMaxValueOfN() {
        StreamStat shtuka = new StreamStat();
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
        assertEquals(2, shtuka.getAvg2());
        assertEquals(1, shtuka.getAvg1());
        assertEquals(Long.MAX_VALUE, shtuka.getCount());


        try {
            shtuka.putNext(1); // Max N is reached.
            assertTrue(false); // PutNext should throw an exception
        } catch (ArithmeticException e) {
            // expected
        }
        assertEquals(2, shtuka.getAvg2()); // getAvg2() method is resistant agains 'set n to arbitrary value via reflection' attack
        assertEquals(1, shtuka.getAvg1()); // getAvg1() mathod can be hacked by modifying n via reflection
        assertEquals(Long.MAX_VALUE, shtuka.getCount());
    }
}