package net.juanlopes.ettree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class L0SamplerJavaTest extends L0SamplerBaseTest<L0SamplerJava> {
    @Override
    public L0SamplerJava create(int m, int d, long seed) {
        return new L0SamplerJava(m, d, seed);
    }

    @Test
    public void testBytes() throws Exception {
        assertThat(create(12, 2, 123L).bytes()).isEqualTo(304);
    }

    @Test
    @Ignore
    public void countSuccesses() throws Exception {

        Random random = new Random();
        int[] V = new int[100000];
        for (int k = 0; k < 10000; k++) {
            long seed = random.nextLong();
            L0SamplerJava sampler1 = create(48, 1, seed);

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
        for (int i = 0; i < 10; i++) {
            mean += i * V[i];
            System.out.println(i + " " + V[i]);
        }
        System.out.println(mean / 10000);
    }
}