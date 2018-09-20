package net.juanlopes.ettree;

import org.junit.Test;

import static org.junit.Assert.*;

public class L0SamplerTest {
    @Test
    public void name() throws Exception {
        L0Sampler sampler = new L0Sampler(17, 1, 0);

        for (int i = 0; i < 1000; i += 1000)
            sampler.update(i, 1);

        System.out.println(sampler.recover());

    }
}