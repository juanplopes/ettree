package net.juanlopes.ettree;

import java.util.Arrays;
import java.util.Locale;

public class L0Sampler implements Mergeable<L0Sampler> {
    private static final long P = PowMod.P;
    public final long[] W0, W1, W2;
    private final long seed;
    private final int m, d;
    private final long originalSeed;

    public L0Sampler(int m, int d, long seed) {
        this.W0 = new long[m * d];
        this.W1 = new long[m * d];
        this.W2 = new long[m * d];
        this.originalSeed = seed;
        this.seed = ((long) MurmurHash.hashLong(seed, 42)) << 32 | MurmurHash.hashLong(seed, 43);
        this.m = m;
        this.d = d;
    }

    public L0Sampler(L0Sampler copy) {
        this.W0 = Arrays.copyOf(copy.W0, copy.W0.length);
        this.W1 = Arrays.copyOf(copy.W1, copy.W1.length);
        this.W2 = Arrays.copyOf(copy.W2, copy.W2.length);
        this.seed = copy.seed;
        this.originalSeed = copy.originalSeed;
        this.m = copy.m;
        this.d = copy.d;
    }


    public void update(long i, long delta) {
        int seed = (int) this.seed ^ (int) (this.seed >>> 32);

        for (int j = 0; j < d; j++) {
            long hash = mer(MurmurHash.hashLong(i, seed),
                    MurmurHash.hashLong(i, seed + 1));
            seed = (int) hash;

            long croppedHash = hash & (1L << m) - 1;
            if (croppedHash == 0) croppedHash++;

            int pos = Long.numberOfLeadingZeros(croppedHash) - (64 - m);
            innerUpdate(j * m + pos, i, delta);
        }
    }

    public long recover() {
        return recover(0, d);
    }

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

    public int bytes() {
        return W0.length * 3 * 8 + 16;
    }

    private int size(int index) {
        if (W0[index] == 0) return 0;
        if (W2[index] != PowMod.op(0, W0[index], z(index), W1[index] / W0[index])) return 2;
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

    public void clearTo(L0Sampler sampler) {
        checkSameArgs(sampler);

        System.arraycopy(sampler.W0, 0, W0, 0, W0.length);
        System.arraycopy(sampler.W1, 0, W1, 0, W1.length);
        System.arraycopy(sampler.W2, 0, W2, 0, W2.length);
    }

    @Override
    public void add(L0Sampler that) {
        checkSameArgs(that);

        for (int i = 0; i < this.W0.length; i++) {
            W0[i] += that.W0[i];
            W1[i] += that.W1[i];
            W2[i] = (W2[i] + that.W2[i]) % P;
        }
    }

    private void checkSameArgs(L0Sampler that) {
        check(this.d, that.d, "Must have same depth: %d != %d");
        check(this.m, that.m, "Must have same width: %d != %d");
        check(this.originalSeed, that.originalSeed, "Must have same seed: %d != %d");
    }


    private void innerUpdate(int index, long i, long delta) {
        W0[index] += delta;
        W1[index] += delta * i;
        W2[index] = PowMod.op(W2[index], delta, z(index), i);
    }

    private long mer(int a, int b) {
        return (long) a << 32 | b & 0xFFFFFFFFL;
    }

    private long z(int index) {
        return mer(MurmurHash.hashLong(index, (int) (this.seed >>> 32)),
                MurmurHash.hashLong(index, (int) (this.seed >>> 32 + 1)));
    }
}
