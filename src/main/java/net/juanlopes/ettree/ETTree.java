package net.juanlopes.ettree;

import java.util.Arrays;
import java.util.function.Supplier;

public class ETTree<T extends Mergeable<T>> {
    private final AVLForest<T> avl = new AVLForest<>();
    private final Supplier<T> emptySupplier;
    private int[] nodes = new int[16];
    private int count = 0;

    public ETTree(Supplier<T> emptySupplier) {
        this.emptySupplier = emptySupplier;
    }

    private void ensureNodesSize(int size) {
        int newSize = 2 * Integer.highestOneBit(size - 1);
        if (newSize > nodes.length)
            nodes = Arrays.copyOf(nodes, newSize);
    }

    public int addNode(T obj) {
        ensureNodesSize(count + 1);
        int index = count++;
        nodes[index] = avl.add(obj);
        return index;
    }

    public long addEdge(int node1, int node2) {
        return addEdge(node1, node2, emptySupplier.get());
    }

    public long addEdge(int node1, int node2, T obj) {
        return addEdge(node1, node2, obj, emptySupplier.get());
    }

    public long addEdge(int node1, int node2, T forward, T backward) {
        int left = nodes[node1];
        int mid = reroot(nodes[node2]);
        int right = avl.cutToLeft(left);

        int a = avl.add(forward);
        int b = avl.add(backward);

        avl.link(avl.link(left, a, mid), b, right);

        return (long) a << 32 | b;
    }

    private int reroot(int child) {
        int childLeft = avl.cutToRight(child);
        return avl.link(child, childLeft);
    }

    public int findRoot(int node) {
        return avl.rootOf(nodes[node]);
    }

    public T findValue(int node) {
        return avl.value(avl.rootOf(nodes[node]));
    }

    public void removeEdge(long id) {
        int a = (int) (id >> 32);
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
