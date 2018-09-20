package net.juanlopes.ettree;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class L0Sampler {
    private final int P = 2147483647;
    private final int[] W0, W1, W2, Z;
    private final int seed;
    private final int m, d;

    public L0Sampler(int m, int d, long seed) {
        this.W0 = new int[m * d];
        this.W1 = new int[m * d];
        this.W2 = new int[m * d];
        this.Z = new int[m * d];
        SecureRandom random = new SecureRandom(ByteBuffer.allocate(8).putLong(seed).array());
        initializeRandomZ(m, d, random);
        this.seed = random.nextInt();
        this.m = m;
        this.d = d;
    }

    private void initializeRandomZ(int m, int d, SecureRandom random) {
        for (int i = 0; i < m * d; i++)
            Z[i] = random.nextInt();
    }

    public void update(int i, long delta) {
        int seed = this.seed;
        for (int j = 0; j < d; j++) {
            long hash = MurmurHash.hashLong(i, seed);
            seed = (int) hash;
            if (hash == 0) continue;

            int pos = Long.numberOfLeadingZeros(hash & ((1 << m) - 1)) - (64 - m);
            innerUpdate(j * m + pos, i, delta);
        }
    }

    private int size(int index) {
        if (W0[index] == 0) return 0;
        if (W2[index] != (W0[index] * ppow(Z[index], W1[index] / W0[index])) % P) return 2;
        return 1;
    }

    int recover() {
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < m; j++) {
                int index = i * m + j;
                if (size(index) == 1)
                    return W1[index] / W0[index];
            }
        }
        return -1;
    }

    private long ppow(long a, long b) {
        long free = 1;
        while (b > 1) {
            if ((b & 1) > 0)
                free = (free * a) % P;
            a = (a * a) % P;
            b >>= 1; //integer divison by 2
        }
        return (a * free) % P;
    }


    private void innerUpdate(int index, int i, long delta) {
        W0[index] += delta;
        W1[index] += delta * i;
        W2[index] += (W2[index] + delta * ppow(Z[i], i)) % P;
    }
}
