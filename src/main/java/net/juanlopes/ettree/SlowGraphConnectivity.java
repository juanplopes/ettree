package net.juanlopes.ettree;

import java.util.Comparator;
import java.util.PriorityQueue;

public class SlowGraphConnectivity {
    private final L0Sampler[] M;
    private final int n;
    private final int d;

    public SlowGraphConnectivity(int n, int d, long seed) {
        this.n = n;
        this.d = d;
        this.M = new L0Sampler[n];
        int m = (int) Math.ceil(Math.log(2 * n) / Math.log(2)) + 5;
        for (int i = 0; i < n; i++)
            M[i] = new L0Sampler(m, d, seed);
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
        UnionFind uf = new UnionFind();
        PriorityQueue<Next> pq = new PriorityQueue<>(Comparator.comparing(x -> x.size));

        for (int i = 0; i < n; i++)
            pq.add(new Next(i, 1));

        int components = n;

        while (components > 1 && !pq.isEmpty()) {
            Next next = pq.poll();
            int v = next.set;

            if (uf.root(v)) {
                int recovered = uf.recover(v);
                if (recovered >= 0) {
                    int a = recovered / n, b = recovered % n;

                    components--;
                    int newSize = uf.union(a, b);
                    int newRoot = uf.find(a);

                    pq.add(new Next(newRoot, newSize));
                } else {
                    //System.out.println("BLEH");
                }
            }
        }

        assert components >= 1;
        return components;
    }

    private static class Next {
        private final int set;
        private final int size;

        public Next(int set, int size) {
            this.set = set;
            this.size = size;
        }
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
                M[i] = new L0Sampler(SlowGraphConnectivity.this.M[i]);
                S[i] = 1;
            }
        }

        public int recover(int v) {
            return M[v].recover();
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
            if (a < b) {
                int c = a;
                a = b;
                b = c;
            }
            M[a].add(M[b]);
            S[a] += S[b];
            P[b] = a;
            return S[a];
        }
    }
}
