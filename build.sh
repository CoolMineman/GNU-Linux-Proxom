cd app
mvn package
cd ..
cd jni
sh ./build.sh
cd ..
rm -r ./build
mkdir build
cp ./app/target/gnu-linux-proxom*.jar ./build
cp ./jni/proxom.so ./build