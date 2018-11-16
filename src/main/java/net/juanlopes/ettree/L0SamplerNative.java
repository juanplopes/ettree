package net.juanlopes.ettree;

import java.io.Closeable;
import java.util.Locale;

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

    private void check(long v1, long v2, String msg) {
        if (v1 != v2)
            throw new IllegalArgumentException(String.format((Locale) null, msg, v1, v2));
    }


    private void checkSameArgs(L0SamplerNative that) {
        check(this.d, that.d, "Must have same depth: %d != %d");
        check(this.m, that.m, "Must have same width: %d != %d");
        check(this.seed, that.seed, "Must have same seed: %d != %d");
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
        checkSameArgs(sampler);
        JNIWrapper.clear(instance, sampler.instance);
    }

    @Override
    public void add(L0SamplerNative other) {
        checkSameArgs(other);
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
