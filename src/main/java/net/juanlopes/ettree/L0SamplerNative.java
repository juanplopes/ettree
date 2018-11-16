package net.juanlopes.ettree;

import java.io.Closeable;

public class L0SamplerNative implements L0Sampler<L0SamplerNative>, Closeable {
    private final int m;
    private final int d;
    private final long seed;
    private long instance;

    public L0SamplerNative(int m, int d, long seed) {
        this.m = m;
        this.d = d;
        this.seed = seed;
        this.instance = JNIWrapper.create(m, d, seed);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void update(long i, long delta) {
        JNIWrapper.update(instance, i, delta);
    }

    public long recover() {
        return recover(0, d);
    }

    public long recover(int start, int end) {
        return JNIWrapper.recover(instance, start, end);
    }

    @Override
    public void clear() {
        JNIWrapper.clear(instance, -1);
    }

    @Override
    public void clearTo(L0SamplerNative sampler) {
        JNIWrapper.clear(instance, sampler.instance);
    }

    @Override
    public void add(L0SamplerNative other) {
        JNIWrapper.merge(instance, other.instance);
    }

    @Override
    public int bytes() {
        return m * d * 3 * 8 + 16;
    }

    @Override
    public void close() {
        if (instance == -1) return;
        JNIWrapper.destroy(instance);
        instance = -1;
    }


}
