package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlowGraphConnectivityTest {
    @Test
    @Ignore
    public void name() throws Exception {
        IntStream.rangeClosed(4, 16).map(x -> (1 << x) - 1).parallel()
                .mapToDouble(this::getErrors).boxed().collect(Collectors.toList());

    }

    private double getErrors(int n) {
        Random random = new Random();
        int errors = 0;
        int tests = 100;
        int d = log2(n)/4;
        for (int k = 0; k < tests; k++) {
            SlowGraphConnectivity G = new SlowGraphConnectivity(n, d, random.nextLong());
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    G.addEdge(i, j);

            if (!G.connected())
                errors++;
        }
        int bytes = new SlowGraphConnectivity(n, d, 42).bytes();

        System.out.println(n + "\t" + errors / (double) tests + "\t" + bytes + "\t" + (n * n / 8));
        return errors / (double) tests;
    }

    private int log2(int n) {
        return 31 - Integer.numberOfLeadingZeros(n);
    }
}