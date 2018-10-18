package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlowGraphConnectivityTest {
    @Test
    public void logggg() throws Exception {
        for (int i = 128; i * i > 0; i *= 1.1) {
            System.out.println(i + "\t" + bytes(i) + "\t" + Math.ceil(i / 8.0 * i));
        }
    }

    @Test
    @Ignore
    public void name2() throws Exception {
        long start = System.currentTimeMillis();
        int n = 10;
        SlowGraphConnectivity G = new SlowGraphConnectivity(n, 1, 100);
        for (int i = 1; i < n; i++) {
            for (int j = i - 1; j >= 0 && j >= i - i; j--)
                G.addEdge(i, j);
            //G.addEdge(i, i - 2);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(G.components(0));
        System.out.println(System.currentTimeMillis() - start);

    }

    @Test
    @Ignore
    public void name() throws Exception {
        for (int i = 128; i * i > 0; i *= 1.1) {
            int n = i;
            double[] errors = getErrors(n);
            String line = Arrays.stream(errors).mapToObj(x -> "\t" + x).collect(Collectors.joining());
            System.out.println(n + line);
        }

    }

    private double[] getErrors(int n) {
        Random random = new Random();
        int tests = 256;

        double[] V = new double[10];
        IntStream.range(0, tests).parallel().mapToObj(x -> test(n, random.nextLong())).forEach(x -> {
            synchronized (V) {
                for (int i = 0; i < V.length; i++)
                    V[i] += x[i];
            }
        });
        for (int i = 0; i < V.length; i++)
            V[i] /= tests;

        return V;
    }

    private int[] test(int n, long v) {
        SlowGraphConnectivity G = new SlowGraphConnectivity(n, 10, v);
        for (int i = 0; i < n; i++) {
            for (int j = i - 1; j >= 0 && j >= i - 8; j--)
                G.addEdge(i, j);
        }

        return IntStream.range(0, 10).map(x -> G.components(x) > 1 ? 1 : 0).toArray();
    }

    private int d(int n) {
        return Math.max(1, (int) Math.ceil(Math.pow(Math.log(n) / Math.log(2), 1)) - 8);
        // return 20;
    }

    private int bytes(int n) {
        return new SlowGraphConnectivity(n, 10, 42).bytes();
    }
}