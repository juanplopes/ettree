package net.juanlopes.ettree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AVLForestTest {
    @Test
    public void testSimple() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 3);

        forest.link(0, 1);
        forest.link(1, 2);

        assertThat(forest.toString()).isEqualTo("(1=6 (0=1) (2=3))");
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

        assertThat(forest.toString()).isEqualTo("(3=28 (1=6 (0=1) (2=3)) (5=18 (4=5) (6=7)))");
    }

    @Test
    public void testSimpleBalanceMore() {
        AVLForest<Slot> forest = new AVLForest<>();

        int N = 6;
        addMany(forest, N);

        for (int i = 0; i < N; i++)
            forest.link(i, i + 1);

        assertThat(forest.toString()).isEqualTo("(3=28 (1=6 (0=1) (2=3)) (5=18 (4=5) (6=7)))");
    }

    private class Slot implements Mergeable<Slot> {
        private final int value;
        private int sum;

        public Slot(int value) {
            this.value = value;
            this.sum = value;
        }

        @Override
        public void clear() {
            sum = value;
        }

        @Override
        public void add(Slot other) {
            sum += other.sum;
        }

        @Override
        public String toString() {
            return "" + sum;
        }
    }
}