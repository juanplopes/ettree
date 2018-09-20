package net.juanlopes.ettree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MurmurHashTest extends MurmurHash {
    @Test
    public void testHashSomethingDummy() throws Exception {
        assertThat(MurmurHash.hashLong(1234L, 1234)).isEqualTo(1022859090);
    }
}