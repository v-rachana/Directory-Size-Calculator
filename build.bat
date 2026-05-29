@echo off
if not exist out mkdir out
javac --release 11 -d out src\*.java
cd out
jar cfe ..\dirsize.jar dirsize.Main dirsize\*.class
cd ..
echo Done! Run with: java -jar dirsize.jar
