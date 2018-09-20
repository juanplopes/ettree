package net.juanlopes.ettree;

public abstract class MurmurHash {
    public static int hashLong(long data, int seed) {
        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ 8;

        int k = (int) data * m;
        k ^= k >>> r;
        h ^= k * m;

        k = (int) (data >> 32) * m;
        k ^= k >>> r;
        h *= m;
        h ^= k * m;

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }
}