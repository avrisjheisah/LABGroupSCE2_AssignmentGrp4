#!/bin/sh
# Compiles all sources into ./out  (needs JDK 11+)
mkdir -p out && javac -d out $(find src -name "*.java") && echo "Build OK"
