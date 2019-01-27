package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

public class FastGraphConnectivityTest {
    @Test
    @Ignore
    public void test1() throws Exception {
        int n = 1000;
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
}