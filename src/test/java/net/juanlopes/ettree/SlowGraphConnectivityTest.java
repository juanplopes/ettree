package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SlowGraphConnectivityTest {
    @Test
    @Ignore
    public void name() throws Exception {
        int nodes = 500, tests = 1024, d = 12;

        int step = nodes / 100;
        double R[][] = new double[100][d - 1];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CountDownLatch finished = new CountDownLatch(tests);
        Random random = new Random();

        for (int test = 0; test < tests; test++) {
            long seed = random.nextLong();
            executor.submit(() -> {
                SlowGraphConnectivity G = new SlowGraphConnectivity(nodes, d, seed);
                int T[] = new int[nodes];
                for (int k = 1; k <= 100; k++) {
                    int start = (k - 1) * step;
                    int end = k * step;

                    for (int i = start; i < end; i++) {
                        for (int j = 0; j < Math.min(32, i); j++)
                            G.addEdge(i, i - j - 1);

                    }
                    for (int i = 1; i < d; i++) {
                        if (G.components(end, i) == 1) {
                            synchronized (R) {
                                R[k - 1][i - 1]++;
                            }
                        }
                    }
                }
                finished.countDown();
            });
        }

        long start = System.nanoTime();
        long lastCount = tests;
        while (!finished.await(1, TimeUnit.SECONDS)) {
            long count = finished.getCount();
            double rate = (System.nanoTime() - start) / (double) (tests - count) / 1e9;
            if (lastCount != count)
                System.out.println((tests - finished.getCount()) + "/" + tests + " (" + Math.round(count * rate) + ")");
            lastCount = count;
        }


        for (int k = 0; k < 100; k++) {
            for (int i = 0; i < R[k].length; i++) {
                R[k][i] /= tests;
            }

            String line = Arrays.stream(R[k]).mapToObj(x -> "\t" + x).collect(Collectors.joining());
            System.out.println((k + 1) * step + line);
        }

    }

}