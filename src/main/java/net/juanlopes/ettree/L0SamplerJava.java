package net.juanlopes.ettree;


import java.util.Arrays;
import java.util.Locale;

public final class L0SamplerJava implements L0Sampler<L0SamplerJava> {
    private static final int P = 2147483647;
    public final int[] W0, W1, W2;
    private final long seed;
    private final int m, d;
    private final long originalSeed;

    public L0SamplerJava(int m, int d, long seed) {
        this.W0 = new int[m * d];
        this.W1 = new int[m * d];
        this.W2 = new int[m * d];
        this.originalSeed = seed;
        this.seed = ((long) MurmurHash.hashLong(seed, 42)) << 32 | MurmurHash.hashLong(seed, P);
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
            innerUpdate(j * m + pos, (int) i, delta);
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
        return W0.length * 3 * 4 + 16;
    }

    private int size(int index) {
        if (W0[index] == 0) return 0;
        if (m(W2[index]) != m(W0[index] * ppow(z(index), m(W1[index] / W0[index])))) return 2;
        return 1;
    }

    private long ppow(long a, long b) {
        long free = 1;
        while (b > 1) {
            if ((b & 1) == 1)
                free = free * a % P;
            a = a * a % P;
            b >>= 1;
        }
        return a * free % P;
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
    public void clearTo(L0SamplerJava sampler) {
        checkSameArgs(sampler);

        System.arraycopy(sampler.W0, 0, W0, 0, W0.length);
        System.arraycopy(sampler.W1, 0, W1, 0, W1.length);
        System.arraycopy(sampler.W2, 0, W2, 0, W2.length);
    }

    @Override
    public void add(L0SamplerJava that) {
        checkSameArgs(that);

        for (int i = 0; i < this.W0.length; i++) {
            W0[i] += that.W0[i];
            W1[i] += that.W1[i];
            W2[i] = m((long) W2[i] + that.W2[i]);
        }
    }

    private void checkSameArgs(L0SamplerJava that) {
        check(this.d, that.d, "Must have same depth: %d != %d");
        check(this.m, that.m, "Must have same width: %d != %d");
        check(this.originalSeed, that.originalSeed, "Must have same seed: %d != %d");
    }


    private int m(long v) {
        long r = v % P;
        return (int) (r < 0 ? r + P : r);
    }

    private void innerUpdate(int index, int i, long delta) {
        W0[index] += delta;
        W1[index] += delta * i;
        W2[index] = m((long) W2[index] + delta * ppow(z(index), i));
    }

    private int z(int index) {
        return m(MurmurHash.hashLong(index, (int) (this.seed >>> 32)));
    }

    @Override
    public void close() {

    }
}
