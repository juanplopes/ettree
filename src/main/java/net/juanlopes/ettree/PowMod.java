package net.juanlopes.ettree;

public abstract class PowMod {
    public static final long P = 4611686018427387847L;

    private static native long modop(long x, long c, long a, long b);

    static {
        System.load("/home/juanplopes/gh/ettree/src/main/cpp/libpowmod.so");
    }

    public static long op(long x, long c, long a, long b) {
        return modop(x, c, a, b);
    }


    public static long fast(long x, long c, long a, long b) {
        return modop(x, c, a, b);
    }

    public static long slow(long x, long c, long a, long b) {
        long free = c % P;
        while (b > 1) {
            if ((b & 1) == 1)
                free = free * a % P;
            a = a * a % P;
            b >>= 1;
        }
        return (x + a * free % P) % P;
    }

}
