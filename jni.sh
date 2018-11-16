#!/bin/bash
mvn clean compile
cd target/classes
javah -d ../../src/main/cpp net.juanlopes.ettree.JNIWrapper