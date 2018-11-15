#include "net_juanlopes_ettree_PowMod.h"
#include <iostream>
#include <cmath>
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

JNIEXPORT jlong JNICALL Java_net_juanlopes_ettree_PowMod_modop
  (JNIEnv* env, jclass obj, jlong c, jlong a, jlong b) {
    return modopint(c, a, b);
}


