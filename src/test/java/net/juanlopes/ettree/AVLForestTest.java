package net.juanlopes.ettree;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AVLForestTest {
    @Test
    public void testSimple() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 3);

        forest.link(0, 1);
        forest.link(1, 2);

        new ForestAssert()
                .component(0, 1, 2)
                .check(forest);
    }

    @Test
    public void testSimpleUpdate() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 3);

        forest.link(0, 1);
        forest.link(1, 2);

        forest.value(2).set(42);
        forest.notifyUpdate(2);

        new ForestAssert()
                .component(0, 1, 2)
                .check(forest);
    }

    @Test
    public void testSimpleInverse() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 3);

        forest.link(2, 1);
        forest.link(1, 0);

        new ForestAssert()
                .component(2, 1, 0)
                .check(forest);
    }

    private void addMany(AVLForest<Slot> forest, int count) {
        for (int i = 0; i < count; i++)
            assertThat(forest.add(new Slot(i + 1))).isEqualTo(i);
    }

    @Test
    public void testSimpleBalance() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 7);

        for (int i = 0; i < 6; i++)
            forest.link(i, i + 1);

        new ForestAssert()
                .component(0, 1, 2, 3, 4, 5, 6)
                .check(forest);
    }

    @Test
    public void testLinkWithRootSingleLeft() throws Exception {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 7);

        for (int i = 0; i < 6; i++)
            forest.linkWithRoot(i, i + 1, -1);

        new ForestAssert()
                .component(0, 1, 2, 3, 4, 5, 6)
                .check(forest);
    }

    @Test
    public void testLinkWithRootSingleRight() throws Exception {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 7);

        for (int i = 0; i < 6; i++)
            forest.linkWithRoot(-1, i, i + 1);

        new ForestAssert()
                .component(0, 1, 2, 3, 4, 5, 6)
                .check(forest);
    }

    @Test
    public void testLinkWithRootSingleRightInverse1() throws Exception {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 7);

        for (int i = 0; i < 6; i++)
            forest.linkWithRoot(-1, i + 1, i);

        new ForestAssert()
                .component(6, 5, 4, 3, 2, 1, 0)
                .check(forest);
    }

    @Test
    public void testLinkWithRootSingleRightInverse() throws Exception {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 7);

        for (int i = 6; i > 0; i--)
            forest.linkWithRoot(i, i - 1, -1);

        new ForestAssert()
                .component(6, 5, 4, 3, 2, 1, 0)
                .check(forest);
    }

    @Test
    public void testBalancedCut() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 14);

        for (int i = 0; i < 13; i++)
            forest.link(i, i + 1);

        forest.cutToLeft(6);
        new ForestAssert()
                .component(0, 1, 2, 3, 4, 5, 6)
                .component(7, 8, 9, 10, 11, 12, 13)
                .check(forest);
    }

    @Test
    public void testBalancedCutRight() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 14);

        for (int i = 0; i < 13; i++)
            forest.link(i, i + 1);

        forest.cutToRight(6);
        new ForestAssert()
                .component(0, 1, 2, 3, 4, 5)
                .component(6, 7, 8, 9, 10, 11, 12, 13)
                .check(forest);
    }

    @Test
    public void testSimpleBalanceMore() {
        AVLForest<Slot> forest = new AVLForest<>();

        int N = 31;
        addMany(forest, N);

        for (int i = 0; i < N - 1; i++)
            forest.link(i, i + 1);

        new ForestAssert()
                .component(IntStream.range(0, 31).toArray())
                .check(forest);
    }

    @Test
    public void testSimpleBalanceMoreOutOfOrder() {
        AVLForest<Slot> forest = new AVLForest<>();

        int N = 31;
        addMany(forest, N);

        for (int i = 0; i < N - 1; i += 2)
            forest.link(i, i + 1);
        for (int i = 1; i < N - 1; i += 4)
            forest.link(i, i + 1);
        for (int i = 3; i < N - 1; i += 8)
            forest.link(i, i + 1);

        new ForestAssert()
                .component(IntStream.range(0, 8).toArray())
                .component(IntStream.range(8, 16).toArray())
                .component(IntStream.range(16, 24).toArray())
                .component(IntStream.range(24, 31).toArray())
                .check(forest);


        forest.link(12, 20);
        forest.link(13, 30);
        forest.link(0, 30);

        new ForestAssert()
                .component(IntStream.range(0, 31).toArray())
                .check(forest);
    }

}

