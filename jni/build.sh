rm ./proxom.so
gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -fPIC -shared -o proxom.so proxom.c