package net.juanlopes.ettree;

import org.assertj.core.data.Offset;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class L0SamplerNativeTest {
    private void assertHappens(double ratio, Function<Integer, Boolean> consumer) {
        int count = 0;
        for (int seed = 0; seed < 2000; seed++) {
            if (consumer.apply(seed))
                count++;
        }
        assertThat(count / 2000.0).isCloseTo(ratio, Offset.offset(0.01));
    }

    @Test
    public void testSimpleSampling() throws Exception {
        assertHappens(0.8118, seed -> {
            L0SamplerNative sampler = new L0SamplerNative(12, 1, seed);
            assertThat(sampler.bytes()).isEqualTo(304);

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
            L0SamplerNative sampler = new L0SamplerNative(12, 2, seed);
            assertThat(sampler.bytes()).isEqualTo(592);

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
        assertHappens(0.6842, seed -> {
            L0SamplerNative sampler = new L0SamplerNative(12, 1, seed);
            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 100);

            L0SamplerNative sampler2 = new L0SamplerNative(12, 1, seed);
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
            L0SamplerNative sampler = new L0SamplerNative(12, 1, seed);

            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 100);

            return sampler.recover() == -1;
        });

    }

    @Test
    public void testEmpty() throws Exception {
        assertHappens(1.0, seed -> {
            L0SamplerNative sampler = new L0SamplerNative(12, 1, seed);

            return sampler.recover() == -1;
        });
    }

    @Test
    public void testSingle() throws Exception {
        assertHappens(1.0, seed -> {
            L0SamplerNative sampler = new L0SamplerNative(12, 1, seed);
            sampler.update(42, 100);

            return sampler.recover() == 42;
        });
    }

    @Test
    public void testTwo() throws Exception {
        assertHappens(2 / 3.0, seed -> {
            L0SamplerNative sampler = new L0SamplerNative(12, 1, seed);
            sampler.update(42, 100);
            sampler.update(43, 100);

            long rec = sampler.recover();
            return rec == 42 || rec == 43;
        });
    }

    @Test
    public void testMergeSampling() throws Exception {
        assertHappens(0.568, seed -> {
            L0SamplerNative sampler1 = new L0SamplerNative(12, 1, seed);
            L0SamplerNative sampler2 = new L0SamplerNative(12, 1, seed);

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
        });
    }

    @Test
    public void cantMergeDifferentSeed() throws Exception {
        L0SamplerNative sampler1 = new L0SamplerNative(25, 1, 160);
        L0SamplerNative sampler2 = new L0SamplerNative(25, 1, 150);

        assertThatThrownBy(() -> sampler1.add(sampler2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Must have same seed: 160 != 150");
    }

    @Test
    public void bug1() throws Exception {
        L0SamplerNative sampler = new L0SamplerNative(32, 11, -3387403153808684640L);
        sampler.update(10000, 1);
        sampler.update(1, -1);
        System.out.println(sampler.recover());
    }

    @Test
    public void bug2() throws Exception {
        for (int i = -100; i < 100; i++) {
            L0SamplerNative s = new L0SamplerNative(12, 1, i);
            L0SamplerNative s0 = new L0SamplerNative(12, 1, i);
            L0SamplerNative s1 = new L0SamplerNative(12, 1, i);

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

    @Test
    @Ignore
    public void name() throws Exception {
        Random random = new Random();
        int count = 0;
        for (int k = 0; k < 10000; k++) {
            L0SamplerNative sampler = new L0SamplerNative(32, 1, random.nextLong());

            sampler.update(10000, 1);
            sampler.update(1, -1);

            long recovered = sampler.recover();
            if (recovered < 0)
                count++;
        }
        System.out.println(count);

    }

    @Test
    @Ignore
    public void countFailures() throws Exception {

        Random random = new Random();
        int[] V = new int[1000];
        int fail = 0;
        for (int k = 0; k < 10000; k++) {
            long seed = random.nextLong();
            L0SamplerNative sampler1 = new L0SamplerNative(10, 4, seed);

            sampler1.update(1, 1);
            sampler1.update(2, 1);

            long recovered = sampler1.recover();
            if (recovered >= 0)
                V[(int) (recovered % 6)]++;
            else
                fail++;

        }

        System.out.println(fail);
        for (int i = 0; i < 6; i++) {
            System.out.println(i + " " + V[i] + " " + (i % 6));
        }
    }

    @Test
    @Ignore
    public void countSuccesses() throws Exception {

        Random random = new Random();
        int[] V = new int[100000];
        for (int k = 0; k < 10000; k++) {
            long seed = random.nextLong();
            L0SamplerNative sampler1 = new L0SamplerNative(48, 1, seed);

            for (int i = 1; i <= 2; i++)
                sampler1.update(i, 1);

            int count = 0;
            long recovered;

            while ((recovered = sampler1.recover()) >= 0) {
                count++;
                sampler1.update(recovered, -1);
            }


            V[count]++;
        }

        double mean = 0;
        for (int i = 0; i < 10; i++) {
            mean += i * V[i];
            System.out.println(i + " " + V[i]);
        }
        System.out.println(mean / 10000);
    }
}