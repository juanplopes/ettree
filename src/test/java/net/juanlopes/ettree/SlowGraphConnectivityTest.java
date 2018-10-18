package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlowGraphConnectivityTest {
    @Test
    @Ignore
    public void name() throws Exception {
        int nodes = 1000, tests = 128, d = 12;

        SlowGraphConnectivity[] G = IntStream.range(0, tests)
                .mapToObj(x -> new SlowGraphConnectivity(nodes, d, x))
                .toArray(SlowGraphConnectivity[]::new);

        int step = nodes / 100;

        for (int k = 0; k < 100; k++) {
            double R[] = new double[d - 1];

            int start = k * step;
            int end = (k + 1) * step;

            IntStream.range(0, tests).parallel().forEach(x -> {
                for (int i = start; i < end; i++) {
                    for (int j = i - 1; j >= 0 && j >= i - 32; j--)
                        G[x].addEdge(i, j);
                }
                for (int i = 1; i < d; i++) {
                    if (G[x].components(end, i) == 1) {
                        synchronized (R) {
                            R[i - 1]++;
                        }
                    }
                }
            });
            for (int i = 0; i < R.length; i++) {
                R[i] /= tests;
            }

            String line = Arrays.stream(R).mapToObj(x -> "\t" + x).collect(Collectors.joining());
            System.out.println(end + line);
        }

    }

}