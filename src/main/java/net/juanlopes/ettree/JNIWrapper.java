package net.juanlopes.ettree;

public abstract class JNIWrapper {
    public static final long P = 4611686018427387847L;

    public static native long powm(long c, long a, long b);

    public static native long create(int m, int d, long seed);

    public static native void destroy(long instance);

    public static native void merge(long instance, long other);

    public static native void clear(long instance, long other);

    public static native void update(long instance, long i, long delta);

    public static native long recover(long instance, int start, int end);

    static {
        System.load("/gh/ettree/src/main/cpp/libl0sampler.so");
    }
}
