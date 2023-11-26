import com.datasiqn.arcadia.rand.WeightedRandom;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import org.junit.Assert;
import org.junit.Test;

import java.text.DecimalFormat;

public class TestWeightedRandom {
    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.##########");

    @Test
    public void test() {
        var weights = new Object2DoubleLinkedOpenHashMap<Values>();
        weights.put(Values.COMMON, 10);     // 62.5%
        weights.put(Values.UNCOMMON, 4);    // 25%
        weights.put(Values.RARE, 2);        // 12.5%
        weights.put(Values.LEGENDARY, 1);   // 6.25%
        WeightedRandom<Values> weightedRandom = new WeightedRandom<>(weights);

        long times = 1_000_000;
        double range = 0.005;
        double total = weights.values().doubleStream().sum();
        System.out.println("Testing " + FORMAT.format(times) + " times with a acceptable range of " + FORMAT.format(range));
        Int2LongMap results = new Int2LongOpenHashMap();

        for (long i = 0; i < times; i++) {
            int random = weightedRandom.generateRandomIndex();
            results.putIfAbsent(random, 0);
            results.computeIfPresent(random, (key, occurrences) -> occurrences + 1);
        }

        {
            int i = 0;
            for (double val : weights.values()) {
                double expected = val / total;
                double deviation = (results.get(i) / (double) times) - expected;
                System.out.println("Expected: " + FORMAT.format(expected) + ".\nDeviation: " + FORMAT.format(deviation));
                Assert.assertTrue(deviation <= range);
                i++;
            }
        }
    }

    private enum Values {
        COMMON,
        UNCOMMON,
        RARE,
        LEGENDARY,
    }
}
