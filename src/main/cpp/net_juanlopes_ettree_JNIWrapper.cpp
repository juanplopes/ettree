#include "net_juanlopes_ettree_JNIWrapper.h"
#include <iostream>
#include <cmath>
#include <cstring>
#define P 4611686018427387847ll

using namespace std;

__int128_t powm(__int128_t a, __int128_t b) {
    __int128_t free = 1;
    while (b > 1) {
        if (b & 1)
            free = free * a % P;
        a = a * a % P;
        b >>= 1;
    }
    return a * free % P;
}

__int128_t modopint(__int128_t c, __int128_t a, __int128_t b) {
    __int128_t r = c*powm(a, b)%P;
    if (r < 0) r+=P;
    return r;
}

uint64_t hashLong(uint64_t data, uint64_t seed) {
    uint64_t c1 = 0x87c37b91114253d5ULL;
    uint64_t c2 = 0x4cf5ad432745937fULL;

    uint64_t h1 = seed, h2 = seed;

    uint64_t k1 = data;
    k1 *= c1;
    k1 = (k1 << 31) | (k1 >> (64 - 31));
    k1 *= c2;
    h1 ^= k1;

    h1 ^= 8;
    h2 ^= 8;

    h1 += h2;
    h2 += h1;

    uint64_t k = h2;
    k ^= k >> 33;
    k *= 0xff51afd7ed558ccdULL;
    k ^= k >> 33;
    k *= 0xc4ceb9fe1a85ec53ULL;
    k ^= k >> 33;

    uint64_t k2 = h1;
    k2 ^= k2 >> 33;
    k2 *= 0xff51afd7ed558ccdULL;
    k2 ^= k2 >> 33;
    k2 *= 0xc4ceb9fe1a85ec53ULL;
    k2 ^= k2 >> 33;

    return (k2 + k);
}

struct L0Sampler {
    int64_t *W0, *W1, *W2;
    uint64_t seed;
    int m, d;

    L0Sampler(int m, int d, int64_t seed) {
        this->W0 = new int64_t[m*d];
        this->W1 = new int64_t[m*d];
        this->W2 = new int64_t[m*d];
        memset(W0, 0, sizeof(int64_t)*m*d);
        memset(W1, 0, sizeof(int64_t)*m*d);
        memset(W2, 0, sizeof(int64_t)*m*d);

        this->seed = hashLong(seed, 123);
        this->m = m;
        this->d = d;
    }

    void update(int64_t i, int64_t delta) {
        uint64_t hash = this->seed;

        for (int j = 0; j < d; j++) {
            hash = hashLong(i, hash);

            uint64_t croppedHash = hash & (1ULL << m) - 1;
            if (croppedHash == 0) croppedHash++;

            int pos = __builtin_clzll(croppedHash) - (64 - m);
            innerUpdate(j * m + pos, i, delta);
        }
    }

    void innerUpdate(int index, int64_t i, int64_t delta) {
        W0[index] += delta;
        W1[index] += delta * i;
        W2[index] = mo(W2[index] + modopint(delta, z(index), i));
    }

    void clear() {
        memset(W0, 0, sizeof(int64_t)*m*d);
        memset(W1, 0, sizeof(int64_t)*m*d);
        memset(W2, 0, sizeof(int64_t)*m*d);
    }

    void clearTo(L0Sampler* other) {
        memcpy(W0, other->W0, sizeof(int64_t)*m*d);
        memcpy(W1, other->W1, sizeof(int64_t)*m*d);
        memcpy(W2, other->W2, sizeof(int64_t)*m*d);
    }

    void merge(L0Sampler* other) {
        for (int i = 0; i < m*d; i++) {
            W0[i] += other->W0[i];
            W1[i] += other->W1[i];
            W2[i] = mo(W2[i] + other->W2[i]);
        }
    }

    uint64_t z(int index) {
        return hashLong(index, seed + 1);
    }

    int64_t mo(int64_t x) {
        if (x>=P) return x-P;
        return x;
    }

    int size(int index) {
        if (W0[index] == 0) return 0;
        if (W2[index] != modopint(W0[index], z(index), W1[index] / W0[index])) return 2;
        return 1;
    }

    int64_t recover(int start, int end) {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < m; j++) {
                int index = i * m + j;
                if (size(index) == 1)
                    return W1[index] / W0[index];
            }
        }
        return -1;
    }

    ~L0Sampler() {
        delete W0;
        delete W1;
        delete W2;
    }
};

JNIEXPORT jlong JNICALL Java_net_juanlopes_ettree_JNIWrapper_powm
  (JNIEnv* env, jclass obj, jlong c, jlong a, jlong b) {
    return modopint(c, a, b);
}

JNIEXPORT jlong JNICALL Java_net_juanlopes_ettree_JNIWrapper_create
  (JNIEnv * env, jclass obj, jint m, jint d, jlong seed) {
    return (jlong)new L0Sampler(m, d, seed);
}

JNIEXPORT void JNICALL Java_net_juanlopes_ettree_JNIWrapper_destroy
  (JNIEnv * env, jclass obj, jlong ptr) {
    delete (L0Sampler*)ptr;
}

JNIEXPORT void JNICALL Java_net_juanlopes_ettree_JNIWrapper_update
  (JNIEnv *, jclass, jlong ptr, jlong i, jlong delta) {
      L0Sampler *sampler = (L0Sampler*)ptr;
      sampler->update(i, delta);
}

JNIEXPORT jlong JNICALL Java_net_juanlopes_ettree_JNIWrapper_recover
  (JNIEnv *, jclass, jlong ptr, jint start, jint end) {
      L0Sampler *sampler = (L0Sampler*)ptr;
      return sampler->recover(start, end);
}

JNIEXPORT void JNICALL Java_net_juanlopes_ettree_JNIWrapper_merge
  (JNIEnv *, jclass, jlong ptr, jlong other) {
      L0Sampler *sampler = (L0Sampler*)ptr;
      sampler->merge((L0Sampler*)other);
}

JNIEXPORT void JNICALL Java_net_juanlopes_ettree_JNIWrapper_clear
  (JNIEnv *, jclass, jlong ptr, jlong other) {
      L0Sampler *sampler = (L0Sampler*)ptr;
      if (other > 0)
        sampler->clearTo((L0Sampler*)other);
      else
        sampler->clear();

}


