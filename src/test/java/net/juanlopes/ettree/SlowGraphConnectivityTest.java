package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

public class SlowGraphConnectivityTest {
    @Test
    public void logggg() throws Exception {
        System.out.println(test(10, 5, 100));
    }

    @Test
    @Ignore
    public void name2() throws Exception {
        long start = System.currentTimeMillis();
        int n = 10;
        SlowGraphConnectivity G = new SlowGraphConnectivity(n, 1, 51);
        for (int i = 1; i < n; i++) {
            for (int j = i - 1; j >= 0 && j >= i - 64; j--)
                G.addEdge(i, j);
            //G.addEdge(i, i - 2);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(G.components());
        System.out.println(System.currentTimeMillis() - start);

    }

    @Test
    @Ignore
    public void name3() throws Exception {
        long count = IntStream.range(0, 100).parallel().mapToLong(x -> {
            return test(512, d(512), x * 2);
        }).filter(x -> x > 1).count();


        System.out.println(count);
    }

    @Test
    @Ignore
    public void name() throws Exception {
        for (int i = 128; i <= 100000; i *= 2) {
            int n = i;
            double x = getErrors(n);
            System.out.println(n + "\t" + x + "\t" + d(n) + "\t" + bytes(n) + "\t" + (n * n / 8));
        }

    }

    private double getErrors(int n) {
        Random random = new Random();
        int tests = 8;
        int d = d(n);

        long errors = IntStream.range(0, tests).parallel().mapToLong(x -> {
            long v = random.nextLong();
            return test(n, d, v);
        }).filter(x -> x > 1).count();

        return errors / (double) tests;
    }

    private long test(int n, int d, long v) {
        SlowGraphConnectivity G = new SlowGraphConnectivity(n, d, v);
        for (int i = 0; i < n; i++) {
            for (int j = i - 1; j >= 0 && j >= i - 1; j--)
                G.addEdge(i, j);
        }

        return G.components();
    }

    private int d(int n) {
        return (int)Math.ceil(Math.log(n)/Math.log(2))-6;
        //return 1;
    }

    private int bytes(int n) {
        return new SlowGraphConnectivity(n, d(n), 42).bytes();
    }
}