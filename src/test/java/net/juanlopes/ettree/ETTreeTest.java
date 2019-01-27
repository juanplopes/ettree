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
    public void testVertexUpdate() throws Exception {
        ETTree<Slot> tree = new ETTree<>(() -> new Slot(0));
        Slot slot1 = new Slot(1);
        int node1 = tree.addNode(slot1);
        Slot slot2 = new Slot(2);
        int node2 = tree.addNode(slot2);
        tree.addEdge(node1, node2);

        assertThat(tree.findValue(node1).sum()).isEqualTo(3);

        slot1.set(42);
        assertThat(tree.findValue(node1).sum()).isEqualTo(3);
        tree.notifyNodeUpdate(node1);
        assertThat(tree.findValue(node1).sum()).isEqualTo(44);

        slot2.set(43);
        assertThat(tree.findValue(node1).sum()).isEqualTo(44);
        tree.notifyNodeUpdate(node2);
        assertThat(tree.findValue(node1).sum()).isEqualTo(85);
    }

    @Test
    public void testEdgeUpdate() throws Exception {
        ETTree<Slot> tree = new ETTree<>(() -> new Slot(0));
        int node1 = tree.addNode(new Slot(1));
        int node2 = tree.addNode(new Slot(2));

        Slot slotA = new Slot(0);
        Slot slotB = new Slot(0);

        long edge = tree.addEdge(node1, node2, slotA, slotB);

        assertThat(tree.findValue(node1).sum()).isEqualTo(3);

        slotA.set(3);
        slotB.set(4);

        assertThat(tree.findValue(node1).sum()).isEqualTo(3);
        tree.notifyEdgeUpdate(edge);
        assertThat(tree.findValue(node1).sum()).isEqualTo(10);
    }

    @Test
    public void testUpdate() throws Exception {
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

    @Test
    public void testThreeEdges() throws Exception {
        ETTree<Slot> tree = new ETTree<>(() -> new Slot(0));
        int n0 = tree.addNode(new Slot(1));
        int n1 = tree.addNode(new Slot(2));
        int n2 = tree.addNode(new Slot(3));

        long e1 = tree.addEdge(n0, n1);
        long e2 = tree.addEdge(n1, n2);
        tree.removeEdge(e1);
        tree.addEdge(n0, n2);
        tree.removeEdge(e2);

        System.out.println(tree.findValue(n0).sum());
        System.out.println(tree.findValue(n1).sum());
        System.out.println(tree.findValue(n2).sum());

        assertThat(tree.findValue(n0).sum()).isEqualTo(4);
        assertThat(tree.findValue(n1).sum()).isEqualTo(2);
        assertThat(tree.findValue(n2).sum()).isEqualTo(4);
    }

    @Test
    public void testSimpleEdges() throws Exception {
        ETTree<Slot> tree = new ETTree<>(() -> new Slot(0));

        int n0 = tree.addNode(new Slot(1, "AA"));
        int n1 = tree.addNode(new Slot(2, "BB"));
        tree.addEdge(n0, n1, new Slot(0, "AB"), new Slot(0, "BA"));
        tree.reroot(n1);

        assertThat(tree.findValue(n0).sum()).isEqualTo(3);
        assertThat(tree.findValue(n1).sum()).isEqualTo(3);
    }
}