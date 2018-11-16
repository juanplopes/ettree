package net.juanlopes.ettree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class L0SamplerNativeTest extends L0SamplerBaseTest<L0SamplerNative> {
    @Override
    public L0SamplerNative create(int m, int d, long seed) {
        return new L0SamplerNative(m, d, seed);
    }

    @Test
    public void testBytes() throws Throwable {
        try (L0SamplerNative sampler = create(12, 2, 123L)) {
            assertThat(sampler.bytes()).isEqualTo(592);
            sampler.finalize();
        }
    }
}