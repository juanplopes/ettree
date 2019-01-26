package net.juanlopes.ettree;

import java.util.HashMap;

public class FastGraphConnectivity {
    private final ETTree<Node> tree;
    private final int n;
    private final int d;
    private final int m;
    private final long seed;
    private final Node[] nodes;
    private final int[] ids;
    private final HashMap<Long, Long> edges;

    public FastGraphConnectivity(int n, int d, long seed) {
        this.n = n;
        this.d = d;
        this.m = (int) Math.ceil(2 * Math.log(n) / Math.log(2)) + 5;
        this.seed = seed;
        this.tree = new ETTree<>(() -> new Node(false));
        this.nodes = new Node[n];
        this.edges = new HashMap<>();
        this.ids = new int[n];
        for (int i = 0; i < n; i++)
            ids[i] = tree.addNode(nodes[i] = new Node(false));
    }

    public int components() {
        return n - edges.size();
    }

    public long addEdge(int a, int b) {
        int aa = Math.min(a, b);
        int bb = Math.max(a, b);
        long edge = (long) aa * n + bb;

        updateNodes(aa, bb, 1);

        if (tree.findRoot(ids[aa]) != tree.findRoot(ids[bb])) {
            System.out.println("ADD " + aa + " " + bb);
            edges.put(edge, tree.addEdge(ids[aa], ids[bb]));
        }

        return edge;
    }

    public void removeEdge(int a, int b) {
        int aa = Math.min(a, b);
        int bb = Math.max(a, b);
        long edge = (long) aa * n + bb;

        updateNodes(aa, bb, -1);

        Long removed = edges.remove(edge);
        if (removed != null) {
            tree.removeEdge(removed);

            Node va = tree.findValue(ids[aa]);
            Node vb = tree.findValue(ids[bb]);
            System.out.println("DEL " + aa + " " + bb);

            if (va.size > vb.size) {
                Node vc = va;
                va = vb;
                vb = vc;
            }

            if (!tryRecoverFrom(va)) {
                tryRecoverFrom(vb);
            }
        }
    }

    private boolean tryRecoverFrom(Node va) {
        long recovered = va.shared.recover();
        if (recovered < 0)
            return false;

        int ea = (int) (recovered / n);
        int eb = (int) (recovered % n);

        System.out.println("ADDX " + ea + " " + eb);
        edges.put(recovered, tree.addEdge(ids[ea], ids[eb]));
        return true;

    }

    private void updateNodes(int aa, int bb, int mult) {
        nodes[aa].my.update((long) aa * n + bb, mult);
        nodes[bb].my.update((long) aa * n + bb, -mult);
        tree.notifyNodeUpdate(ids[aa]);
        tree.notifyNodeUpdate(ids[bb]);
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
