#!/bin/bash
# build.sh — compile and package (requires Java 11+, no Maven needed)
set -e
mkdir -p out
javac --release 11 -d out src/*.java
cd out
jar cfe ../dirsize.jar dirsize.Main $(find . -name "*.class")
cd ..
echo "Done! Run with: java -jar dirsize.jar"
