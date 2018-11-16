package net.juanlopes.ettree;

import org.assertj.core.data.Offset;
import org.junit.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class L0SamplerBaseTest<T extends L0Sampler<T>> {
    public abstract T create(int m, int d, long seed);

    private void assertHappens(double ratio, Function<Integer, Boolean> consumer) {
        int count = 0;
        for (int seed = 0; seed < 2000; seed++) {
            if (consumer.apply(seed))
                count++;
        }
        assertThat(count / 2000.0).isCloseTo(ratio, Offset.offset(0.025));
    }

    @Test
    public void testSimpleSampling() throws Exception {
        assertHappens(0.8118, seed -> {
            T sampler = create(12, 1, seed);

            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 100);

            long recovered = sampler.recover();
            if (recovered < 0) return false;

            assertThat(recovered).isBetween(0L, 198L);
            assertThat(recovered % 2).isZero();
            return true;
        });
    }

    @Test
    public void testSimpleSamplingDouble() throws Exception {
        assertHappens(1 - (0.1882 * 0.1882), seed -> {
            T sampler = create(12, 2, seed);

            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 100);

            long recovered = sampler.recover();
            if (recovered < 0) return false;

            assertThat(recovered).isBetween(0L, 198L);
            assertThat(recovered % 2).isZero();
            return true;
        });
    }

    @Test
    public void testSimpleSamplingOnClearTo() throws Exception {
        assertHappens(0.6945, seed -> {
            T sampler = create(12, 1, seed);
            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 100);

            T sampler2 = create(12, 1, seed);
            sampler2.clearTo(sampler);

            for (int i = 100; i < 200; i++)
                sampler2.update(i * 2, 100);

            long rec1 = sampler.recover();
            long rec2 = sampler2.recover();
            if (rec1 < 0 || rec2 < 0) return false;
            assertThat(rec1).isBetween(0L, 198L);
            assertThat(rec2).isBetween(0L, 398L);
            return true;
        });
    }

    @Test
    public void testCantRecover() throws Exception {
        assertHappens(0.1882, seed -> {
            T sampler = create(12, 1, seed);

            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 100);

            return sampler.recover() == -1;
        });

    }

    @Test
    public void testEmpty() throws Exception {
        assertHappens(1.0, seed -> {
            try (T sampler = create(12, 1, seed)) {
                return sampler.recover() == -1;
            }
        });
    }

    @Test
    public void testSingle() throws Exception {
        assertHappens(1.0, seed -> {
            try (T sampler = create(12, 1, seed)) {
                sampler.update(42, 100);

                return sampler.recover() == 42;
            }
        });
    }

    @Test
    public void testTwo() throws Exception {
        assertHappens(2 / 3.0, seed -> {
            try (T sampler = create(12, 1, seed)) {
                sampler.update(42, 100);
                sampler.update(43, 100);

                long rec = sampler.recover();
                return rec == 42 || rec == 43;
            }
        });
    }

    @Test
    public void testMergeSampling() throws Exception {
        assertHappens(0.5535, seed -> {
            try (T sampler1 = create(12, 1, seed);
                 T sampler2 = create(12, 1, seed)) {

                for (int i = 0; i < 50; i++) {
                    sampler1.update(i * 2, 100);
                }
                for (int i = 50; i < 100; i++) {
                    sampler2.update(i * 2, 100);
                }

                long rec1 = sampler1.recover();
                if (rec1 < 0) return false;
                assertThat(rec1).isBetween(0L, 98L);

                sampler1.add(sampler2);

                long rec1a = sampler1.recover();
                if (rec1a < 0) return false;
                assertThat(rec1a).isBetween(0L, 198L);

                long rec2a = sampler2.recover();
                if (rec2a < 0) return false;
                assertThat(rec2a).isBetween(100L, 198L);

                sampler1.clear();
                long rec1b = sampler1.recover();
                assertThat(rec1b).isEqualTo(-1);

                long rec2b = sampler2.recover();
                if (rec2b < 0) return false;
                assertThat(rec2b).isBetween(100L, 198L);

                return true;
            }
        });
    }

    @Test
    public void cantMergeDifferentSeed() throws Exception {
        try (T sampler1 = create(25, 1, 160); T sampler2 = create(25, 1, 150)) {
            assertThatThrownBy(() -> sampler1.add(sampler2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Must have same seed: 160 != 150");
        }
    }

    @Test
    public void bug1() throws Exception {
        try (T sampler = create(32, 11, -3387403153808684640L)) {
            sampler.update(10000, 1);
            sampler.update(1, -1);
            System.out.println(sampler.recover());
        }
    }

    @Test
    public void bug2() throws Exception {
        for (int i = -100; i < 100; i++) {
            try (T s = create(12, 1, i); T s0 = create(12, 1, i); T s1 = create(12, 1, i)) {
                s.update(1, 1);
                s0.update(1, 1);

                s.update(1, -1);
                s1.update(1, -1);

                s.update(12, 1);
                s1.update(12, 1);
                s0.add(s1);
                assertThat(s0.recover()).isGreaterThan(0).describedAs("seed: %d", i).isEqualTo(s.recover());
            }
        }
    }
}