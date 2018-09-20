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

        for (int i = 0; i < 100; i++)
            sampler.update(i * 2, 100);

        assertThat(sampler.recover()).isEqualTo(124);
    }

    @Test
    public void testCantRecover() throws Exception {
        L0Sampler sampler = new L0Sampler(25, 1, 150);

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
        L0Sampler sampler1 = new L0Sampler(25, 1, 160);
        L0Sampler sampler2 = new L0Sampler(25, 1, 160);

        for (int i = 0; i < 50; i++)
            sampler1.update(i * 2, 100);
        for (int i = 50; i < 100; i++)
            sampler2.update(i * 2, 100);
        sampler1.merge(sampler2);

        assertThat(sampler1.recover()).isEqualTo(124);
    }

    @Test
    public void cantMergeDifferentSeed() throws Exception {
        L0Sampler sampler1 = new L0Sampler(25, 1, 160);
        L0Sampler sampler2 = new L0Sampler(25, 1, 150);

        assertThatThrownBy(()->sampler1.merge(sampler2))
                .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Must have same seed: -95752431 != 1896773833");
    }

    @Test
    @Ignore
    public void name() throws Exception {
        Random random = new Random();
        int count = 0;
        for (int k = 0; k < 1000; k++) {
            L0Sampler sampler = new L0Sampler(25, 1, random.nextLong());

            for (int i = 0; i < 1000000; i++)
                sampler.update(i * 2, 1);

            int recovered = sampler.recover();
            if (recovered < 0)
                count++;
        }
        System.out.println(count);

    }
}