# GNU-Linux-Proxom
Simple GNU Linux proxy made for playing Among Us(http://www.innersloth.com/gameAmongUs.php) in p2p mode. This proxy is based on the "sudppipe" project, which can be found here: https://aluigi.altervista.org/mytoolz.htm.

To start the proxy, start the jar with the server ip you are connecting to as an argument.
Example: `java -jar gnu-linux-proxom-0.0.1.jar 54.56.34.62`

## Compile Prerequisites
To use you must have a JDK installed. (Java 8 has been tested, most versions probably work)
The path to the JDK folder must be in the variable `JAVA_HOME`.
Example: `export JAVA_HOME=/path/to/jdk/folder`

You'll need to put `libproxom.so`, which is a product of the compilation process, in your native path. This can be accomplished by either putting `libproxom.so` in `/usr/lib` after compiling, or putting `.` in the native path.
Example: `export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.`

You can also put the export commands in your `~/.bashrc` or `~/.bash_profile` to add the variables when opening the terminal.

## Compiling Instructions
To compile, go into the folder where you cloned the project, and run the script `build.sh` located in that folder, then in the `build` folder you'll find `gnu-linux-proxom-0.0.1.jar` and `libproxom.so`.
