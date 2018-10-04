package net.juanlopes.ettree;

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

        int components = n;

        int layers = (int) Math.ceil(Math.log(n) / Math.log(2));
        for (int i = 0; i < layers; i++) {
            for (int v = 0; v < n; v++) {
                if (uf.root(v)) {
                    int recovered = uf.recover(v);
                    if (recovered >= 0) {
                        int a = recovered / n, b = recovered % n;

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
            return a;
        }
    }
}