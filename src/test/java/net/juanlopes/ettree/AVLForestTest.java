package net.juanlopes.ettree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AVLForestTest {
    @Test
    public void testSimple() {
        AVLForest<Slot> forest = new AVLForest<>();

        addMany(forest, 3);

        forest.link(0, 1);
        forest.link(1, 2);

        assertThat(forest.toString()).isEqualTo("digraph{v0[label=\"1(1)\"];v1[label=\"6(2)\"];v2[label=\"3(3)\"];v1->v0;v1->v2;}");
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

        assertThat(forest.toString()).isEqualTo("digraph{v0[label=\"1(1)\"];v1[label=\"6(2)\"];v2[label=\"3(3)\"];v3[label=\"28(4)\"];v4[label=\"5(5)\"];v5[label=\"18(6)\"];v6[label=\"7(7)\"];v3->v1;v1->v0;v1->v2;v3->v5;v5->v4;v5->v6;}");
    }

    @Test
    public void testSimpleBalanceMore() {
        AVLForest<Slot> forest = new AVLForest<>();

        int N = 31;
        addMany(forest, N);

        for (int i = 0; i < N - 1; i++)
            forest.link(i, i + 1);

        assertThat(forest.toString()).isEqualTo(
                "digraph{v0[label=\"1(1)\"];v1[label=\"6(2)\"];v2[label=\"3(3)\"];v3[label=\"28(4)\"];v4[label=\"5(5)\"];v5[label=\"18(6)\"];v6[label=\"7(7)\"];v7[label=\"120(8)\"];v8[label=\"9(9)\"];v9[label=\"30(10)\"];v10[label=\"11(11)\"];v11[label=\"84(12)\"];v12[label=\"13(13)\"];v13[label=\"42(14)\"];v14[label=\"15(15)\"];v15[label=\"496(16)\"];v16[label=\"17(17)\"];v17[label=\"54(18)\"];v18[label=\"19(19)\"];v19[label=\"140(20)\"];v20[label=\"21(21)\"];v21[label=\"66(22)\"];v22[label=\"23(23)\"];v23[label=\"360(24)\"];v24[label=\"25(25)\"];v25[label=\"78(26)\"];v26[label=\"27(27)\"];v27[label=\"196(28)\"];v28[label=\"29(29)\"];v29[label=\"90(30)\"];v30[label=\"31(31)\"];v15->v7;v7->v3;v3->v1;v1->v0;v1->v2;v3->v5;v5->v4;v5->v6;v7->v11;v11->v9;v9->v8;v9->v10;v11->v13;v13->v12;v13->v14;v15->v23;v23->v19;v19->v17;v17->v16;v17->v18;v19->v21;v21->v20;v21->v22;v23->v27;v27->v25;v25->v24;v25->v26;v27->v29;v29->v28;v29->v30;}"
        );
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

        forest.link(12, 20);
        forest.link(13, 30);
        forest.link(0, 30);

        assertThat(forest.toString()).isEqualTo(
                "digraph{v0[label=\"1(1)\"];v1[label=\"6(2)\"];v2[label=\"3(3)\"];v3[label=\"28(4)\"];v4[label=\"5(5)\"];v5[label=\"18(6)\"];v6[label=\"7(7)\"];v7[label=\"120(8)\"];v8[label=\"9(9)\"];v9[label=\"30(10)\"];v10[label=\"11(11)\"];v11[label=\"84(12)\"];v12[label=\"13(13)\"];v13[label=\"42(14)\"];v14[label=\"15(15)\"];v15[label=\"496(16)\"];v16[label=\"17(17)\"];v17[label=\"54(18)\"];v18[label=\"19(19)\"];v19[label=\"140(20)\"];v20[label=\"21(21)\"];v21[label=\"66(22)\"];v22[label=\"23(23)\"];v23[label=\"360(24)\"];v24[label=\"25(25)\"];v25[label=\"78(26)\"];v26[label=\"27(27)\"];v27[label=\"196(28)\"];v28[label=\"29(29)\"];v29[label=\"90(30)\"];v30[label=\"31(31)\"];v15->v7;v7->v3;v3->v1;v1->v0;v1->v2;v3->v5;v5->v4;v5->v6;v7->v11;v11->v9;v9->v8;v9->v10;v11->v13;v13->v12;v13->v14;v15->v23;v23->v19;v19->v17;v17->v16;v17->v18;v19->v21;v21->v20;v21->v22;v23->v27;v27->v25;v25->v24;v25->v26;v27->v29;v29->v28;v29->v30;}"
        );
        //System.out.println(forest.toString());
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
            return sum + "(" + value + ")";
        }
    }
}

