package net.juanlopes.ettree;

import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PowModTest extends PowMod {
    @Test
    public void test() throws Exception {
        long f = PowMod.fast(1, -10, 1);
        assertThat(f).isEqualTo(PowMod.P - 10);
    }

    @Test
    public void testBigPowNormal() throws Exception {
        long a = 2;
        long b = 30;
        long p = PowMod.P;
        long r = BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(p)).longValue();

        assertThat(PowMod.fast(1, a, b)).isEqualTo(r).isEqualTo(1 << 30);
        assertThat(PowMod.slow(1, a, b)).isEqualTo(r);
    }

    @Test
    public void testBigPowNormalWithXandC() throws Exception {
        long a = 2;
        long b = 20;
        long r = 3 * (1 << 20);

        assertThat(PowMod.fast(3, a, b)).isEqualTo(r);
        assertThat(PowMod.slow(3, a, b)).isEqualTo(r);
    }

    @Test
    public void testBigPow2() throws Exception {
        long a = 123456789123456789L;
        long b = 987654321987654321L;
        long p = PowMod.P;
        long r = BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(p)).longValue();

        assertThat(PowMod.fast(1, a, b)).isEqualTo(r);
        assertThat(PowMod.slow(1, a, b)).isNotEqualTo(r);
    }

    @Test
    public void testBigPow2Full() throws Exception {
        long a = 123456789123456789L;
        long b = 987654321987654321L;
        long p = PowMod.P;
        long r = BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(p)).longValue();
        r = BigInteger.valueOf(0).add(BigInteger.valueOf(3).multiply(BigInteger.valueOf(r)).mod(BigInteger.valueOf(p))).longValue();

        assertThat(PowMod.fast(3, a, b)).isEqualTo(r).isEqualTo(4125482591540598862L);
        assertThat(PowMod.slow(3, a, b)).isNotEqualTo(r);
    }
}