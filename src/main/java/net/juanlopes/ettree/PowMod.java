package net.juanlopes.ettree;

public class PowMod {
//    public static native long boostPowM(long a, long b, long p);
//
//    static {
//        //System.load("/gh/ettree/src/main/cpp/libpowmod.so");
//    }
//
//    public static long fast(long a, long b, long p) {
//        return boostPowM(a, b, p);
//    }
//
    public static long slow(long a, long b, long p) {
        long free = 1;
        while (b > 1) {
            if ((b & 1) == 1)
                free = free * a % p;
            a = a * a % p;
            b >>= 1;
        }
        return a * free % p;
    }

}
