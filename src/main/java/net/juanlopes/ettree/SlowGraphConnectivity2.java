package net.juanlopes.ettree;

public class SlowGraphConnectivity2 {
    private final L0SamplerJava[] M;
    private long seed;
    private final UnionFind uf;

    private final int n;
    private final int d;
    private final int m;

    public SlowGraphConnectivity2(int n, int d, long seed) {
        this.n = n;
        this.d = d;
        this.m = (int) Math.ceil(2 * Math.log(n) / Math.log(2)) + 5;
        this.seed = seed;
        this.M = new L0SamplerJava[n];
        for (int i = 0; i < n; i++)
            M[i] = new L0SamplerJava(m, d, seed);
        this.uf = new UnionFind();
    }

    public void addEdge(int a, int b) {
        int aa = Math.min(a, b);
        int bb = Math.max(a, b);

        M[aa].update((long) aa * n + bb, 1);
        M[bb].update((long) aa * n + bb, -1);
    }

    public void removeEdge(int a, int b) {
        int aa = Math.min(a, b);
        int bb = Math.max(a, b);

        M[aa].update(aa * n + bb, -1);
        M[bb].update(aa * n + bb, 1);
    }

    public int bytes() {
        return M[0].bytes() * n + 8;
    }

    public int components() {
        return components(n, d);
    }

    public int components(int nodes, int limit) {
        uf.init(nodes);

        int layers = limit;
        int components = nodes;
        while (layers-- > 0 && components > 1 && uf.recover(nodes, limit)) {
            for (int v = 0; v < nodes; v++) {
                long recovered = uf.E[v];
                if (recovered >= 0) {
                    int a = (int) (recovered / n), b = (int) (recovered % n);
                    if (uf.union(a, b)) {
                        components--;
                    }
                }
            }
        }

        assert components >= 1;
        return components;
    }

    private class UnionFind {
        private final int[] P;
        private final L0SamplerJava[] M;
        private final int S[];
        private final long E[];

        public UnionFind() {
            this.P = new int[n];
            this.M = new L0SamplerJava[n];
            this.S = new int[n];
            this.E = new long[n];

            for (int i = 0; i < n; i++) {
                P[i] = i;
                M[i] = new L0SamplerJava(m, d, seed);
                S[i] = 1;
            }
        }

        public void init(int n) {
            for (int i = 0; i < n; i++) {
                M[i].clearTo(SlowGraphConnectivity2.this.M[i]);
                P[i] = i;
                S[i] = 1;
            }
        }

        public boolean recover(int n, int limit) {
            boolean answer = false;
            for (int i = 0; i < n; i++) {
                E[i] = P[i] == i ?
                        M[i].recover(0, limit) :
                        -1;
                if (E[i] >= 0)
                    answer = true;
            }
            return answer;
        }

        public int find(int v) {
            if (P[v] != v)
                return P[v] = find(P[v]);
            return v;
        }

        public boolean union(int x, int y) {
            int a = find(x), b = find(y);
            if (a == b) return false;
            if (S[a] < S[b]) {
                int c = a;
                a = b;
                b = c;
            }
            M[a].add(M[b]);
            S[a] += S[b];
            P[b] = a;
            return true;
        }
    }
}
