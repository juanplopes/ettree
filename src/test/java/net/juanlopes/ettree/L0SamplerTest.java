package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class L0SamplerTest {
    @Test
    public void testSimpleSampling() throws Exception {
        L0Sampler sampler = new L0Sampler(25, 1, 160);
        assertThat(sampler.bytes()).isEqualTo(316);

        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(22);
    }

    @Test
    public void testSimpleSamplingOnCopy() throws Exception {
        L0Sampler sampler = new L0Sampler(25, 1, 160);
        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        L0Sampler sampler2 = new L0Sampler(sampler);

        for (int i = 100; i < 200; i++)
            sampler2.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(22);
        assertThat(sampler2.recover()).isEqualTo(296);
    }

    @Test
    public void testCantRecover() throws Exception {
        L0Sampler sampler = new L0Sampler(25, 1, 25);

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

        assertThat(sampler1.recover()).isEqualTo(28);

        sampler1.add(sampler2);

        assertThat(sampler1.recover()).isEqualTo(152);
        assertThat(sampler2.recover()).isEqualTo(150);

        sampler1.clear();
        assertThat(sampler1.recover()).isEqualTo(-1);
        assertThat(sampler2.recover()).isEqualTo(150);
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
    @Ignore
    public void name() throws Exception {
        Random random = new Random();
        int count = 0;
        for (int k = 0; k < 10000; k++) {
            L0Sampler sampler = new L0Sampler(25, 1, random.nextLong());

            for (int i = 0; i < 100; i++)
                sampler.update(i * 2, 1);

            int recovered = sampler.recover();
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

            int recovered = sampler1.recover();
            if (recovered >= 0)
                V[recovered % 6]++;
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
        int[] V = new int[100];
        for (int k = 0; k < 10000; k++) {
            long seed = random.nextLong();
            L0Sampler sampler1 = new L0Sampler(20, 10, seed);

            for (int i = 0; i < 1024; i++)
                sampler1.update(i * 123, 1);

            int count = 0, recovered;

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