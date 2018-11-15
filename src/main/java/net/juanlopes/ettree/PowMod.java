package net.juanlopes.ettree;

public abstract class PowMod {
    public static final long P = 4611686018427387847L;

    private static native long modop(long c, long a, long b);

    static {
        System.load("/home/juanplopes/gh/ettree/src/main/cpp/libpowmod.so");
    }

    public static long op(long c, long a, long b) {
        return modop(c, a, b);
    }


    public static long fast(long c, long a, long b) {
        return modop(c, a, b);
    }

    public static long slow(long c, long a, long b) {
        long free = c % P;
        while (b > 1) {
            if ((b & 1) == 1)
                free = free * a % P;
            a = a * a % P;
            b >>= 1;
        }
        return (a * free % P) % P;
    }

}
