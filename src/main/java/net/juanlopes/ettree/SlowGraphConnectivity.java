package net.juanlopes.ettree;

import java.util.Arrays;

public class SlowGraphConnectivity {
    private final L0Sampler[] M;
    private long seed;
    private final UnionFind uf;


    private final int n;
    private final int d;
    private final int m;

    public SlowGraphConnectivity(int n, int d, long seed) {
        this.n = n;
        this.d = d;
        this.m = (int) Math.ceil(2 * Math.log(n) / Math.log(2)) + 5;
        this.seed = seed;
        this.M = new L0Sampler[n];
        for (int i = 0; i < n; i++)
            M[i] = new L0Sampler(m, d, seed);
        this.uf = new UnionFind();
    }

    public void addEdge(int a, int b) {
        M[a].update(a * n + b, 1);
        M[a].update(b * n + a, -1);
        M[b].update(a * n + b, -1);
        M[b].update(b * n + a, 1);
    }

    public void removeEdge(int a, int b) {
        M[a].update(a * n + b, -1);
        M[a].update(b * n + a, 1);
        M[b].update(a * n + b, 1);
        M[b].update(b * n + a, -1);
    }

    public int bytes() {
        return M[0].bytes() * n + 8;
    }

    public int components() {
        return components(n, d);
    }

    public int components(int nodes, int limit) {
        uf.init(nodes);
        int components = nodes;

        int layers = (int) Math.ceil(Math.log(n) / Math.log(2));
        for (int i = 0; i < layers && components > 1; i++) {
            for (int v = 0; v < nodes && components > 1; v++) {
                if (uf.root(v)) {
                    int recovered = uf.recover(v, limit);
                    if (recovered >= 0) {
                        int a = recovered / n, b = recovered % n;
                        assert uf.find(a) != uf.find(b);

                        components--;
                        uf.union(a, b);
                    }
                }
            }
        }

        assert components >= 1;
        return components;
    }

    private class UnionFind {
        private final int[] P;
        private final L0Sampler[] M;
        private final int S[];

        public UnionFind() {
            this.P = new int[n];
            this.M = new L0Sampler[n];
            this.S = new int[n];

            for (int i = 0; i < n; i++) {
                P[i] = i;
                M[i] = new L0Sampler(m, d, seed);
                S[i] = 1;
            }
        }

        public void init(int n) {
            for (int i = 0; i < n; i++) {
                M[i].clearTo(SlowGraphConnectivity.this.M[i]);
                P[i] = i;
                S[i] = 1;
            }
        }

        public int recover(int v, int limit) {
            return M[v].recover(0, limit);
        }


        public boolean root(int v) {
            return P[v] == v;
        }

        public int find(int v) {
            if (!root(v))
                return P[v] = find(P[v]);
            return v;
        }

        public int union(int x, int y) {
            int a = find(x), b = find(y);
            if (S[a] < S[b]) {
                int c = a;
                a = b;
                b = c;
            }
            M[a].add(M[b]);
            S[a] += S[b];
            P[b] = a;
            return a;
        }
    }
}