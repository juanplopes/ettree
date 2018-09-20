package net.juanlopes.ettree;

public class SlowGraphConnectivity {
    private final L0Sampler[] M;
    private final int n;
    private final int d;

    public SlowGraphConnectivity(int n, int d, long seed) {
        this.n = n;
        this.d = d;
        this.M = new L0Sampler[n];
        for (int i = 0; i < n; i++)
            M[i] = new L0Sampler((int) Math.ceil(Math.log(2 * n) / Math.log(2)) + 5, d, seed);
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
        return M[0].bytes() + 8;
    }

    public boolean connected() {
        UnionFind uf = new UnionFind();
        int components = n;
        int roundOps;

        do {
            roundOps = 0;

            for (int i = 0; i < n; i++) {
                if (uf.root(i)) {
                    int recovered = uf.recover(i);
                    if (recovered >= 0) {
                        int a = recovered / n, b = recovered % n;
                        roundOps++;
                        components--;
                        uf.union(a, b);
                    }
                }
            }
        } while (roundOps > 0);

        return components == 1;
    }

    private class UnionFind {
        private final int[] P;
        private final L0Sampler[] M;

        public UnionFind() {
            this.P = new int[n];
            this.M = new L0Sampler[n];

            for (int i = 0; i < n; i++) {
                P[i] = i;
                M[i] = new L0Sampler(SlowGraphConnectivity.this.M[i]);
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

        public void union(int x, int y) {
            int a = find(x), b = find(y);
            if (a < b) {
                int c = a;
                a = b;
                b = c;
            }
            M[a].add(M[b]);
            P[b] = a;
        }
    }
}
