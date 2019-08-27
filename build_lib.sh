#!/bin/bash
JAVA_HOME=`type -p javac|xargs readlink -f|xargs dirname|xargs dirname`
cd src/main/cpp
mkdir -p ../resources
g++ -std=c++11 -shared -fPIC -O3 -I/usr/include -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" net_juanlopes_ettree_JNIWrapper.cpp -o ../resources/libl0sampler.so
