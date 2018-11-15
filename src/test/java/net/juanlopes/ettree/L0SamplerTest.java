package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class L0SamplerTest {
    @Test
    public void testSimpleSampling() throws Exception {
        L0Sampler sampler = new L0Sampler(24, 1, 160);
        assertThat(sampler.bytes()).isEqualTo(304);

        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(10);
    }

    @Test
    public void testSimpleSamplingOnCopy() throws Exception {
        L0Sampler sampler = new L0Sampler(24, 1, 160);
        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        L0Sampler sampler2 = new L0Sampler(sampler);

        for (int i = 100; i < 200; i++)
            sampler2.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(10);
        assertThat(sampler2.recover()).isEqualTo(324);
    }

    @Test
    public void testSimpleSamplingOnClearTo() throws Exception {
        L0Sampler sampler = new L0Sampler(24, 1, 160);
        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        L0Sampler sampler2 = new L0Sampler(24, 1, 160);
        sampler2.clearTo(sampler);

        for (int i = 100; i < 200; i++)
            sampler2.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(10);
        assertThat(sampler2.recover()).isEqualTo(324);
    }

    @Test
    public void testCantRecover() throws Exception {
        L0Sampler sampler = new L0Sampler(25, 1, 26);

        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(-1);
    }

    @Test
    public void testEmpty() throws Exception {
        L0Sampler sampler = new L0Sampler(25, 1, 150);

        assertThat(sampler.recover()).isEqualTo(-1);
    }

    @Test
    public void testMergeSampling() throws Exception {
        L0Sampler sampler1 = new L0Sampler(25, 2, 7);
        L0Sampler sampler2 = new L0Sampler(25, 2, 7);

        for (int i = 0; i < 50; i++) {
            sampler1.update(i * 2, 100);
        }
        for (int i = 50; i < 100; i++) {
            sampler2.update(i * 2, 100);
        }

        assertThat(sampler1.recover()).isEqualTo(12);

        sampler1.add(sampler2);

        assertThat(sampler1.recover()).isEqualTo(166);
        assertThat(sampler2.recover()).isEqualTo(166);

        sampler1.clear();
        assertThat(sampler1.recover()).isEqualTo(-1);
        assertThat(sampler2.recover()).isEqualTo(166);
    }

    @Test
    public void cantMergeDifferentSeed() throws Exception {
        L0Sampler sampler1 = new L0Sampler(25, 1, 160);
        L0Sampler sampler2 = new L0Sampler(25, 1, 150);

        assertThatThrownBy(() -> sampler1.add(sampler2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Must have same seed: 160 != 150");
    }

    @Test
    public void bug1() throws Exception {
        L0Sampler sampler = new L0Sampler(32, 11, -3387403153808684640L);
        sampler.update(10000, 1);
        sampler.update(1, -1);
        System.out.println(sampler.recover());
    }

    @Test
    public void bug2() throws Exception {
        for (int i = -100; i < 100; i++) {
            L0Sampler s = new L0Sampler(12, 1, i);
            L0Sampler s0 = new L0Sampler(12, 1, i);
            L0Sampler s1 = new L0Sampler(12, 1, i);

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
            L0Sampler sampler = new L0Sampler(32, 1, random.nextLong());

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
            L0Sampler sampler1 = new L0Sampler(10, 4, seed);

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
            L0Sampler sampler1 = new L0Sampler(48, 1, seed);

            for (int i = 1; i <= 100; i++)
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
        for (int i = 0; i < 40; i++) {
            mean += i * V[i];
            System.out.println(i + " " + V[i]);
        }
        System.out.println(mean / 10000);
    }
}