rm ./proxom.so
gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -fPIC -shared -o libproxom.so proxom.c