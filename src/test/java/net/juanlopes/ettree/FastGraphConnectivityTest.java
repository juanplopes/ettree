package net.juanlopes.ettree;

import org.junit.Test;

public class FastGraphConnectivityTest {
    @Test
    public void test1() throws Exception {
        FastGraphConnectivity G = new FastGraphConnectivity(3, 10, 123);
        for (int i = 0; i < 3; i++) {
            for (int j = i - 1; j >= Math.max(0, i - 32); j--) {
                G.addEdge(i, j);
            }
        }
        for (int i = 1; i < 3; i++) {
            G.removeEdge(i, i - 1);
        }
        System.out.println(G.components());
    }
}