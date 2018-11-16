package net.juanlopes.ettree;

public abstract class MurmurHash {

    public static long hashLong(long data, long seed) {
        long c1 = 0x87c37b91114253d5L;
        long c2 = 0x4cf5ad432745937fL;

        long h1 = seed, h2 = seed;

        long k1 = data;
        k1 *= c1;
        k1 = (k1 << 31) | (k1 >>> (64 - 31));
        k1 *= c2;
        h1 ^= k1;

        h1 ^= 8;
        h2 ^= 8;

        h1 += h2;
        h2 += h1;

        long k = h2;
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;

        long k2 = h1;
        k2 ^= k2 >>> 33;
        k2 *= 0xff51afd7ed558ccdL;
        k2 ^= k2 >>> 33;
        k2 *= 0xc4ceb9fe1a85ec53L;
        k2 ^= k2 >>> 33;

        return (k2 + k);
    }
}