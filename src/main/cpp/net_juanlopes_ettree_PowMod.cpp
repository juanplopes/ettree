#include "net_juanlopes_ettree_PowMod.h"
#include <iostream>
#include <cmath>

using namespace std;

long long powm(long long a, long long b, long long p) {
    long long free = 1;
    while (b > 1) {
        if ((b & 1) == 1)
            free = free * a % p;
        a = a * a % p;
        b >>= 1;
    }
    return a * free % p;
}


JNIEXPORT jlong JNICALL Java_net_juanlopes_ettree_PowMod_boostPowM
  (JNIEnv* env, jclass obj, jlong a, jlong b, jlong p) {
    return powm(a, b, p);
}

int main() {
    long long x = powm(2, 20, 10);
    cout << x << endl;
    return 0;
}

