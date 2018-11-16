package net.juanlopes.ettree;

import java.util.Arrays;
import java.util.Locale;

public final class L0SamplerDefault implements L0Sampler<L0SamplerDefault> {
    private static final long P = JNIWrapper.P;
    public final long[] W0, W1, W2;
    private final long seed;
    private final int m, d;
    private final long originalSeed;

    public L0SamplerDefault(int m, int d, long seed) {
        this.W0 = new long[m * d];
        this.W1 = new long[m * d];
        this.W2 = new long[m * d];
        this.originalSeed = seed;
        this.seed = MurmurHash.hashLong(seed, 123L);
        this.m = m;
        this.d = d;
    }


    @Override
    public void update(long i, long delta) {
        long hash = this.seed;

        for (int j = 0; j < d; j++) {
            hash = MurmurHash.hashLong(i, hash);

            long croppedHash = hash & (1L << m) - 1;
            if (croppedHash == 0) croppedHash++;

            int pos = Long.numberOfLeadingZeros(croppedHash) - (64 - m);
            innerUpdate(j * m + pos, i, delta);
        }
    }

    @Override
    public long recover() {
        return recover(0, d);
    }

    @Override
    public long recover(int start, int end) {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < m; j++) {
                int index = i * m + j;
                if (size(index) == 1)
                    return W1[index] / W0[index];
            }
        }
        return -1;
    }

    @Override
    public int bytes() {
        return W0.length * 3 * 8 + 16;
    }

    private int size(int index) {
        if (W0[index] == 0) return 0;
        if (W2[index] != JNIWrapper.powm(W0[index], z(index), W1[index] / W0[index])) return 2;
        return 1;
    }

    private void check(long v1, long v2, String msg) {
        if (v1 != v2)
            throw new IllegalArgumentException(String.format((Locale) null, msg, v1, v2));
    }

    @Override
    public void clear() {
        Arrays.fill(W0, 0);
        Arrays.fill(W1, 0);
        Arrays.fill(W2, 0);
    }

    @Override
    public void clearTo(L0SamplerDefault sampler) {
        checkSameArgs(sampler);

        System.arraycopy(sampler.W0, 0, W0, 0, W0.length);
        System.arraycopy(sampler.W1, 0, W1, 0, W1.length);
        System.arraycopy(sampler.W2, 0, W2, 0, W2.length);
    }

    @Override
    public void add(L0SamplerDefault that) {
        checkSameArgs(that);

        for (int i = 0; i < this.W0.length; i++) {
            W0[i] += that.W0[i];
            W1[i] += that.W1[i];
            W2[i] = m(W2[i] + that.W2[i]);
        }
    }

    private void checkSameArgs(L0SamplerDefault that) {
        check(this.d, that.d, "Must have same depth: %d != %d");
        check(this.m, that.m, "Must have same width: %d != %d");
        check(this.originalSeed, that.originalSeed, "Must have same seed: %d != %d");
    }


    private void innerUpdate(int index, long i, long delta) {
        W0[index] += delta;
        W1[index] += delta * i;
        W2[index] = m(W2[index] + JNIWrapper.powm(delta, z(index), i));
    }

    private long m(long a) {
        if (a >= P) return a - P;
        return a;
    }

    private long z(int index) {
        return MurmurHash.hashLong(index, this.seed + 1);
    }
}
