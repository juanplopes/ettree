package net.juanlopes.ettree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ForestAssert {
    private final List<List<Integer>> components = new ArrayList<>();

    public ForestAssert component(int... nodes) {
        if (nodes.length == 0) return this;
        components.add(IntStream.of(nodes).boxed().collect(Collectors.toList()));
        return this;
    }

    public ForestAssert check(AVLForest<Slot> forest) {
        assertThat(makeComponents(forest)).isEqualTo(new HashSet<>(components));
        assertInvariants(forest);
        return this;
    }

    public static void assertInvariants(AVLForest<Slot> forest) {
        for (int i = 0; i < forest.count(); i++) {
            if (i == forest.rootOf(i)) {
                assertInvariants(forest, -1, i);
            }
        }
    }

    private static void assertInvariants(AVLForest<Slot> forest, int p, int v) {
        if (v < 0) return;

        assertThat(forest.parent(v)).isEqualTo(p);

        assertInvariants(forest, v, forest.left(v));
        assertInvariants(forest, v, forest.right(v));

        Slot slot1 = new Slot(0), slot2 = new Slot(0);
        int height1 = 0, height2 = 0;
        if (forest.left(v) >= 0) {
            slot1 = forest.value(forest.left(v));
            height1 = forest.height(forest.left(v));
        }
        if (forest.right(v) >= 0) {
            slot2 = forest.value(forest.right(v));
            height2 = forest.height(forest.right(v));
        }

        Slot slot = forest.value(v);
        assertThat(slot.sum()).isEqualTo(slot.value() + slot1.sum() + slot2.sum());
        assertThat(forest.height(v)).isEqualTo(1 + Math.max(height1, height2));
        assertThat(Math.abs(height1 - height2)).isLessThanOrEqualTo(1);
    }


    public static Set<List<Integer>> makeComponents(AVLForest<?> forest) {
        Set<List<Integer>> set = new HashSet<>();
        for (int i = 0; i < forest.count(); i++) {
            if (i == forest.rootOf(i)) {
                List<Integer> comp = new ArrayList<>();
                makeComponent(comp, forest, i);
                set.add(comp);
            }
        }
        return set;
    }

    private static void makeComponent(List<Integer> list, AVLForest<?> forest, int v) {
        if (v < 0) return;

        makeComponent(list, forest, forest.left(v));
        list.add(v);
        makeComponent(list, forest, forest.right(v));
    }

    public static String print(AVLForest<?> forest) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph{graph[ordering=\"out\"];");

        for (int i = 0; i < forest.count(); i++) {
            builder.append("v").append(i).append("[label=\"").append(forest.value(i)).append("\"];");
        }

        boolean[] visited = new boolean[forest.count()];
        for (int i = 0; i < forest.count(); i++) {
            if (i == forest.rootOf(i))
                print(builder, forest, -1, i);
        }

        return builder.append("}").toString();
    }

    private static void print(StringBuilder builder, AVLForest<?> forest, int p, int v) {
        if (v < 0) return;
        if (p >= 0)
            builder.append("v").append(p).append("->").append("v").append(v).append(";");

        print(builder, forest, v, forest.left(v));
        print(builder, forest, v, forest.right(v));
    }

}
