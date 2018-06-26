package net.juanlopes.ettree;

import org.omg.CORBA.OBJ_ADAPTER;

import java.util.Arrays;

public class AVLForest<T extends Mergeable<T>> {
    private int[] parent, left, right, height;
    private T[] value;
    private int count;

    public AVLForest() {
        parent = new int[16];
        left = new int[16];
        right = new int[16];
        height = new int[16];
        value = (T[]) new Mergeable[16];
        count = 0;
    }

    private void ensureSize(int size) {
        int newSize = 2 * Integer.highestOneBit(size - 1);
        if (newSize > value.length) {
            parent = Arrays.copyOf(parent, newSize);
            left = Arrays.copyOf(left, newSize);
            right = Arrays.copyOf(right, newSize);
            height = Arrays.copyOf(height, newSize);
            value = Arrays.copyOf(value, newSize);
        }
    }

    public int count() {
        return count;
    }

    public int height(int node) {
        if (node < 0) return 0;
        return height[node];
    }

    public int parent(int node) {
        if (node < 0) return -1;
        return parent[node];
    }

    public int add(T obj) {
        ensureSize(count + 1);
        int index = count++;
        parent[index] = -1;
        left[index] = -1;
        right[index] = -1;
        height[index] = 1;
        value[index] = obj;
        return index;
    }

    public int rootOf(int node) {
        while (parent(node) >= 0) {
            node = parent[node];
        }
        return node;
    }

    public int link(int node1, int node2) {
        return linkByRoot(rootOf(node1), rootOf(node2));
    }

    private int unlink(int node) {
        int parentNode = parent[node];
        if (parentNode < 0) return -1;

        right[parentNode] = left[node];
        update(parentNode);

        parent[node] = left[node] = right[node] = -1;
        update(node);

        return balanceTree(parentNode);
    }

    private int linkByRoot(int node1, int node2) {
        int root = findRightmost(node1);
        node1 = unlink(root);

        return linkWithRoot(node1, node2, root);
    }

    private int linkWithRoot(int node1, int node2, int root) {
        node1 = adjustHeight(node1, node2, right);
        node2 = adjustHeight(node2, node1, left);

        int p1 = parent(node1), p2 = parent(node2);

        left[root] = node1;
        right[root] = node2;
        update(root);

        reroot(root, p1, right);
        reroot(root, p2, left);
        return balanceTree(root);
    }

    private void reroot(int root, int p1, int[] side) {
        if (p1 >= 0) {
            parent[root] = p1;
            side[p1] = root;
            update(p1);
        }
    }

    private int adjustHeight(int node1, int node2, int[] dir) {
        while (height(node1) - height(node2) > 1)
            node1 = dir[node1];
        return node1;
    }

    private int findRightmost(int node1) {
        int node = node1;
        while (right[node] >= 0)
            node = right[node];
        return node;
    }

    private void update(int node) {
        value[node].clear();
        height[node] = 1 + Math.max(height(left[node]), height(right[node]));

        if (left[node] >= 0) {
            if (value[left[node]] != null)
                value[node].add(value[left[node]]);
            parent[left[node]] = node;
        }
        if (right[node] >= 0) {
            if (value[right[node]] != null)
                value[node].add(value[right[node]]);
            parent[right[node]] = node;
        }
    }

    private int factor(int node) {
        return height(right[node]) - height(left[node]);
    }

    private int balanceTree(int node) {
        while (true) {
            int parentNode = parent[node];
            int newNode = balance(node);

            if (parentNode < 0)
                return newNode;

            if (left[parentNode] == node)
                left[parentNode] = newNode;
            else
                right[parentNode] = newNode;

            update(parentNode);
            node = parentNode;
        }
    }

    private int balance(int node) {
        if (node < 0) return -1;
        int rootFactor = factor(node);
        if (rootFactor < -1) {
            if (factor(left[node]) > 0) {
                left[node] = rotateLeft(left[node]);
                update(node);
            }
            return rotateRight(node);
        } else if (rootFactor > 1) {
            if (factor(right[node]) < 0) {
                right[node] = rotateRight(right[node]);
                update(node);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private int rotateLeft(int node) {
        return rotate(node, right, left);
    }

    private int rotateRight(int node) {
        return rotate(node, left, right);
    }

    private int rotate(int node, int[] from, int[] to) {
        int temp = from[node];
        parent[temp] = parent[node];
        from[node] = to[temp];
        to[temp] = node;

        update(node);
        update(temp);
        return temp;
    }

    @Override
    public String toString() {
        boolean[] visited = new boolean[count];
        StringBuilder builder = new StringBuilder();
        builder.append("digraph{");

        for (int i = 0; i < count; i++) {
            builder.append("v").append(i).append("[label=\"").append(value[i]).append("\"];");
        }
        for (int i = 0; i < count; i++) {
            int root = rootOf(i);
            if (!visited[root])
                print(builder, visited, root);
        }

        return builder.append("}").toString();
    }

    private void print(StringBuilder builder, boolean visited[], int v) {
        if (visited[v]) {
            builder.append("/bug/");
            return;
        }
        visited[v] = true;
        if (left[v] >= 0) {
            builder.append("v").append(v).append("->").append("v").append(left[v]).append(";");
            print(builder, visited, left[v]);
        }

        if (right[v] >= 0) {
            builder.append("v").append(v).append("->").append("v").append(right[v]).append(";");
            print(builder, visited, right[v]);
        }
    }

    public void cutToRight(int node) {
        cut(node, false);
    }

    public void cutToLeft(int node) {
        cut(node, true);
    }

    private void cut(int node, boolean keepLeft) {
        int leftTree = keepLeft ? -1 : left[node];
        int rightTree = keepLeft ? right[node] : -1;

        while (node >= 0) {
            System.out.println(node);
            if (keepLeft) {
                int tree = left[node];
                unlinkChildren(node);
                leftTree = linkWithRoot(tree, leftTree, node);
            } else {
                int tree = right[node];
                unlinkChildren(node);
                rightTree = linkWithRoot(rightTree, tree, node);
            }
            int last = node;
            node = parent(node);
            keepLeft = node >= 0 && right[node] == last;
        }

    }

    private void unlinkChildren(int node) {
        left[node] = -1;
        right[node] = -1;
        update(node);
    }
}
