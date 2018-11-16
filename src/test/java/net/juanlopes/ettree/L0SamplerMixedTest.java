package net.juanlopes.ettree;

import org.assertj.core.data.Offset;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class L0SamplerMixedTest extends L0SamplerBaseTest<L0SamplerMixed> {
    @Override
    public L0SamplerMixed create(int m, int d, long seed) {
        return new L0SamplerMixed(m, d, seed);
    }

    @Test
    public void testBytes() throws Exception {
        assertThat(create(12, 2, 123L).bytes()).isEqualTo(592);
    }
}