package net.juanlopes.ettree;

import java.util.Arrays;

public class ETTree<T extends Mergeable<T>> {
    private final AVLForest<T> euler = new AVLForest<>();
    private int[] start = new int[16];
    private int[] end = new int[16];
    private int count = 0;

    private void ensureSize(int size) {
        int newSize = 2 * Integer.highestOneBit(size - 1);
        if (newSize > start.length) {
            start = Arrays.copyOf(start, newSize);
            end = Arrays.copyOf(end, newSize);
        }
    }

    public int add(T obj) {
        ensureSize(count + 1);
        int index = count++;
        start[index] = euler.add(obj);
        return index;
    }

    public void link(int child, int parent) {

    }

    public void cut(int child) {

    }

}
