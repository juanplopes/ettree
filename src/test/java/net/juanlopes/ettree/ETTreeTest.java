package net.juanlopes.ettree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ETTreeTest {
    @Test
    public void testSimple() throws Exception {
        ETTree<Slot> tree = new ETTree<>(() -> new Slot(0));
        int node1 = tree.addNode(new Slot(1));
        int node2 = tree.addNode(new Slot(2));
        long edge12 = tree.addEdge(node1, node2);

        assertThat(tree.findRoot(node1)).isEqualTo(tree.findRoot(node2));
        tree.removeEdge(edge12);
        assertThat(tree.findRoot(node1)).isNotEqualTo(tree.findRoot(node2));
    }

    @Test
    public void testPairOdd() throws Exception {
        ETTree<Slot> tree = new ETTree<>(() -> new Slot(0));
        int K = 30;
        int[] nodes = new int[2 * K];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = tree.addNode(new Slot(i + 1));
            if (i >= 2)
                tree.addEdge(nodes[i], nodes[i - 2]);
        }

        int comp1 = tree.findRoot(nodes[0]);
        int comp2 = tree.findRoot(nodes[1]);

        for (int i = 0; i < nodes.length - 2; i += 2) {
            assertThat(tree.findRoot(nodes[i])).isEqualTo(comp1);
            assertThat(tree.findRoot(nodes[i + 1])).isEqualTo(comp2);
        }

        assertThat(tree.findValue(nodes[0]).sum()).isEqualTo(K * K);
        assertThat(tree.findValue(nodes[1]).sum()).isEqualTo(K * K + K);

        long edge = tree.addEdge(nodes[7], nodes[6]);

        assertThat(tree.findValue(nodes[0]).sum()).isEqualTo(2 * K * K + K);
        assertThat(tree.findValue(nodes[1]).sum()).isEqualTo(2 * K * K + K);

        tree.removeEdge(edge);

        assertThat(tree.findValue(nodes[0]).sum()).isEqualTo(K * K);
        assertThat(tree.findValue(nodes[1]).sum()).isEqualTo(K * K + K);

        edge = tree.addEdge(nodes[7], nodes[6]);

        assertThat(tree.findValue(nodes[0]).sum()).isEqualTo(2 * K * K + K);
        assertThat(tree.findValue(nodes[1]).sum()).isEqualTo(2 * K * K + K);
        long revEdge = edge >> 32 | edge << 32;
        tree.removeEdge(revEdge);

        assertThat(tree.findValue(nodes[0]).sum()).isEqualTo(K * K);
        assertThat(tree.findValue(nodes[1]).sum()).isEqualTo(K * K + K);
    }


}