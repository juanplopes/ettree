package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SlowGraphConnectivityTest {
    @Test
    @Ignore
    public void name() throws Exception {
        int nodes = 20000, tests = 128, d = 10, steps = 1;

        int step = nodes / steps;
        double R[][] = new double[steps][d - 1];
        int C[][] = new int[steps][d - 1];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CountDownLatch finished = new CountDownLatch(tests);
        AtomicLong progress = new AtomicLong(0);
        Random random = new Random();

        for (int test = 0; test < tests; test++) {
            long seed = random.nextLong();
            executor.submit(() -> {
                try {
                    SlowGraphConnectivity G = new SlowGraphConnectivity(nodes, d, seed);
                    for (int k = 0; k < steps; k++) {
                        int start = k * step;
                        int end = (k + 1) * step;

                        for (int i = start; i < end; i++) {
                            for (int j = 0; j < Math.min(32, i); j++)
                                G.addEdge(i, i - j - 1);

                            progress.incrementAndGet();
                        }
                        for (int i = 1; i < d; i++) {
                            C[k][i - 1]++;
                            if (G.components(end, i) == 1) {
                                synchronized (R) {
                                    R[k][i - 1]++;
                                }
                            }
                        }
                    }
                    finished.countDown();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        long start = System.nanoTime();
        long total = nodes * tests;
        long lastCount = 0;
        while (!finished.await(1, TimeUnit.SECONDS)) {
            long count = progress.get();
            long now = System.nanoTime();
            double rate = (double) count / (now - start) * 1e9;
            if (lastCount != count) {
                System.out.println(String.format((Locale) null,
                        "%d/%d (%d) rate: %.0f time: %d",
                        count,
                        total,
                        Math.round((total - count) / rate),
                        rate,
                        Math.round((now - start) / 1e9)));
            }
            lastCount = count;
        }


        for (int k = 0; k < steps; k++) {
            for (int i = 0; i < R[k].length; i++) {
                assert C[k][i] == tests;
                R[k][i] /= C[k][i];
            }

            String line = Arrays.stream(R[k]).mapToObj(x -> "\t" + x).collect(Collectors.joining());
            System.out.println((k + 1) * step + line);
        }

    }

}