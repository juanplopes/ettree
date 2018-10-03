package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlowGraphConnectivityTest {
    @Test
    public void logggg() throws Exception {
        for (int i = 2; i <= (1 << 20); i *= 2)
            System.out.println(i + " " + d(i));
    }

    @Test
    @Ignore
    public void name2() throws Exception {
        long start = System.currentTimeMillis();
        int n = 8192;
        SlowGraphConnectivity G = new SlowGraphConnectivity(n, 10, new Random().nextInt());
        for (int i = 2; i < n; i++) {
            G.addEdge(i, i - 1);
            G.addEdge(i, i - 2);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(G.components());
        System.out.println(System.currentTimeMillis() - start);

    }

    @Test
    @Ignore
    public void name() throws Exception {
        for (int i = 128; i <= 8192 * 4; i += 128) {
            int n = i;
            double x = getErrors(n);
            System.out.println(n + "\t" + x + "\t" + d(n) + "\t" + bytes(n) + "\t" + (n * n / 8));
        }

    }

    private double getErrors(int n) {
        Random random = new Random();
        int tests = 16;
        int d = d(n);

        long errors = IntStream.range(0, tests).parallel().mapToLong(x -> {
            SlowGraphConnectivity G = new SlowGraphConnectivity(n, d, random.nextLong());
            for (int i = 0; i < n; i++) {
                for (int j = i - 1; j >= 0 && j >= i - 1; j--)
                    G.addEdge(i, j);
            }

            return G.components();
        }).filter(x -> x > 1).count();

        return errors / (double) tests;
    }

    private int d(int n) {
        return (int) (Math.pow(Math.log(n), 1));
    }

    private int bytes(int n) {
        return new SlowGraphConnectivity(n, d(n), 42).bytes();
    }

    private int log2(int n) {
        return 31 - Integer.numberOfLeadingZeros(n);
    }
}