package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FastGraphConnectivityTest {
    @Test
    @Ignore
    public void test1() throws Exception {
        int n = 10000;
        FastGraphConnectivity G = new FastGraphConnectivity(n, 10, 123);
        for (int i = 0; i < n; i++) {
            for (int j = i - 1; j >= Math.max(0, i - 32); j--) {
                G.addEdge(i, j);
            }
        }
        for (int i = 1; i < n; i++) {
            G.removeEdge(i, i - 1);
        }
        System.out.println(G.components());
    }


    @Test
    @Ignore
    public void name() throws Exception {
        int nodes = 10000, tests = 2048, d = 4, steps = 50;

        int step = nodes / steps;
        double R[] = new double[steps];
        int C[] = new int[steps];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CountDownLatch finished = new CountDownLatch(tests);
        AtomicLong progress = new AtomicLong(0);
        Random random = new Random();

        for (int test = 0; test < tests; test++) {
            long seed = random.nextLong();
            executor.submit(() -> {
                try {
                    Random local = new Random(seed);
                    FastGraphConnectivity G = new FastGraphConnectivity(nodes, d, seed);
                    for (int k = 0; k < steps; k++) {
                        int start = k * step;
                        int end = (k + 1) * step;

                        for (int i = start; i < end; i++) {
                            if (i - 1 >= 0)
                                G.addEdge(i, i - 1);

                            if (i - 1 > 0)
                                for (int j = i - 2; j >= i - 32 && j >= 0; j--)
                                    G.addEdge(i, j); //local.nextInt(i - 1)

                            progress.incrementAndGet();
                        }
                        for (int i = start; i < end; i++) {
                            if (i - 1 >= 0)
                                G.removeEdge(i, i - 1);
                        }
                        //System.out.println(G.components(end, i));

                        int comp = G.components(end);

                        synchronized (R) {
                            if (comp == 1)
                                R[k]++;
                            C[k]++;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    finished.countDown();
                }
            });
        }

        long start = System.nanoTime();
        long total = nodes * tests;
        long lastCount = 0;
        int iters = 0;
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

            if (++iters % 600 == 0)
                printr(steps, step, R, C);

        }
        printr(steps, step, R, C);

    }

    private void printr(int steps, int step, double[] R, int[] C) {
        for (int k = 0; k < steps; k++) {
            double perc = R[k] / Math.max(C[k], 1);
            String line = "\t" + perc;
            System.out.println((k + 1) * step + line);
        }
    }

}