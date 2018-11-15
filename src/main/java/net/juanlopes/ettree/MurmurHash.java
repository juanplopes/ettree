package net.juanlopes.ettree;

public abstract class MurmurHash {

    private static long rotateLeft64(long x, int r) {
        return (x << r) | (x >>> (64 - r));
    }

    private static long fmix(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;

        return k;
    }

    public static long hashLong(long data, long seed) {
        long c1 = 0x87c37b91114253d5L;
        long c2 = 0x4cf5ad432745937fL;

        long h1 = seed, h2 = seed;

        long k1 = data;
        k1 *= c1;
        k1 = rotateLeft64(k1, 31);
        k1 *= c2;
        h1 ^= k1;

        h1 ^= 8;
        h2 ^= 8;

        h1 += h2;
        h2 += h1;

        return (fmix(h1) + fmix(h2));
    }
}