#!/bin/bash
cd src/main/cpp
g++ -std=c++11 -shared -fPIC -O3 -I/usr/include -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" net_juanlopes_ettree_JNIWrapper.cpp -o libl0sampler.so