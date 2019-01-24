package net.juanlopes.ettree;

public class FastGraphConnectivity {
    private final ETTree<Node> tree;
    private final int n;
    private final int d;
    private final int m;
    private final long seed;
    private final Node[] nodes;
    private final int[] ids;

    public FastGraphConnectivity(int n, int d, long seed) {
        this.n = n;
        this.d = d;
        this.m = (int) Math.ceil(2 * Math.log(n) / Math.log(2)) + 5;
        this.seed = seed;
        this.tree = new ETTree<>(() -> new Node(false));
        this.nodes = new Node[n];
        this.ids = new int[n];
        for (int i = 0; i < n; i++)
            ids[i] = tree.addNode(nodes[i] = new Node(false));
    }

    public long addEdge(int a, int b) {
        int aa = Math.min(a, b);
        int bb = Math.max(a, b);

        nodes[aa].my.update((long) aa * n + bb, 1);
        nodes[bb].my.update((long) aa * n + bb, 1);
        //tree.

        return aa * n + bb;
    }

    public void removeEdge(int a, int b) {
        int aa = Math.min(a, b);
        int bb = Math.max(a, b);

        //M[aa].update(aa * n + bb, -1);
        //M[bb].update(aa * n + bb, 1);
    }


    private class Node implements Mergeable<Node> {
        private final L0SamplerJava my = new L0SamplerJava(m, d, seed);
        private final L0SamplerJava shared = new L0SamplerJava(m, d, seed);
        private final boolean empty;
        private int size = 0;


        public Node(boolean empty) {
            this.empty = empty;
        }

        @Override
        public void clear() {
            shared.clearTo(my);
            size = empty ? 0 : 1;
        }

        @Override
        public void add(Node other) {
            shared.add(other.shared);
            size += other.size;
        }
    }
}
