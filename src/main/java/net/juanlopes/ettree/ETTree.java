package net.juanlopes.ettree;

import java.util.function.Supplier;

public class ETTree<T extends Mergeable<T>> {
    private final AVLForest<T> avl = new AVLForest<>();
    private final Supplier<T> emptySupplier;

    public ETTree(Supplier<T> emptySupplier) {
        this.emptySupplier = emptySupplier;
    }

    public int addNode(T obj) {
        return avl.add(obj);
    }

    public void notifyNodeUpdate(int node) {
        avl.notifyUpdate(node);
    }

    public void notifyForwardEdgeUpdate(long edge) {
        int a = (int) (edge >>> 32);
        avl.notifyUpdate(a);
    }

    public void notifyBackwardsEdgeUpdate(long edge) {
        int b = (int) edge;
        avl.notifyUpdate(b);
    }

    public void notifyEdgeUpdate(long edge) {
        notifyForwardEdgeUpdate(edge);
        notifyBackwardsEdgeUpdate(edge);
    }

    public long addEdge(int node1, int node2) {
        return addEdge(node1, node2, emptySupplier.get());
    }

    public long addEdge(int node1, int node2, T obj) {
        return addEdge(node1, node2, obj, emptySupplier.get());
    }

    public long addEdge(int node1, int node2, T forward, T backward) {
        int left = node1;
        int mid = reroot(node2);
        int right = avl.cutToLeft(left);

        int a = avl.add(forward);
        int b = avl.add(backward);

        avl.link(avl.link(left, a, mid), b, right);

        return (((long) a) << 32) | b;
    }

    public int reroot(int child) {
        int childLeft = avl.cutToRight(child);
        return avl.link(child, childLeft);
    }

    public int findRoot(int node) {
        return avl.rootOf(node);
    }

    public T findValue(int node) {
        return avl.value(avl.rootOf(node));
    }

    public void removeEdge(long id) {
        int a = (int) (id >>> 32);
        int b = (int) id;

        int leftA = avl.cutToRight(a);

        if (avl.rootOf(b) == avl.rootOf(a)) {
            avl.link(leftA, avl.cutToLeft(b));
        } else {
            avl.link(a, avl.cutToRight(b));
        }
        avl.remove(a);
        avl.remove(b);
    }

}
