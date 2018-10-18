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
        int nodes = 1000, tests = 2048, d = 12;

        int step = nodes / 100;
        double R[][] = new double[100][d - 1];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CountDownLatch finished = new CountDownLatch(tests);
        Random random = new Random();

        for (int test = 0; test < tests; test++) {
            long seed = random.nextLong();
            executor.submit(() -> {
                SlowGraphConnectivity G = new SlowGraphConnectivity(nodes, d, seed);
                for (int k = 0; k < 100; k++) {
                    int start = k * step;
                    int end = (k + 1) * step;

                    for (int i = start; i < end; i++) {
                        for (int j = i - 1; j >= 0 && j >= i - 32; j--)
                            G.addEdge(i, j);
                    }
                    for (int i = 1; i < d; i++) {
                        if (G.components(end, i) == 1) {
                            synchronized (R) {
                                R[k][i - 1]++;
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
                System.out.println(finished.getCount() + "/" + tests + " (" + Math.round(count * rate) + ")");
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