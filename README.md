Neko: Remote File System for CX4013 Distributed System.

# Neko CLI

To use Neko CLI, we use the following command to generate Neko CLI binary:

```Shell
$ gradle installDist
```

Two files will be generated at `build/install/neko-file-system/bin/` folder, i.e. `neko-file-system` (for MAC OS
and Linux) and `neko-file-system` (for Windows). We run the CLI by:

```Shell
$ cd build/install/neko-file-system/bin/

$ ./neko-file-system
# You should get the following output
# usage: neko [OPTIONS]...
#  -help   print this message
#
# usage: neko read [ARGS]
#  -b,--byte <arg>     the number of byte to be read
#  -o,--offset <arg>   read bytes starting from this offset
#
# usage: neko insert [ARGS]
#  -o,--offset <arg>   insert bytes starting from this offset
#     --text <arg>     textOption to be inserted
#
# usage: neko monitor [ARGS]
#     --time <arg>   time intervals in milliseconds
#
# usage: neko copy <path>
#
# usage: neko count <path>
```

Here are some examplesg:

```Shell
# Read 500 bytes, starting from the first byte, from the given file
$ ./neko-file-system read -b 500 -o 0 /home/andy/Documents/HelloWorld.txt

# Insert the string "hello world\n", where "\n" represents a new line character,
# starting from the first byte, to the given file
$ ./neko-file-system insert -o 0 --text "hello world\n" /home/andy/Documents/HelloWorld.txt
```

# Getting Started

First, get an IDE (IntelliJ or Eclipse) and import this project using Gradle. 

Next, run your server using the following commands

```Shell
# This is just temporary, as we will move these to a CLI tools
$ cd src/main/java/com/neko
$ javac UDPServer.java
$ java -cp . UDPServer
```

Next, run your client using the following commands

```Shell
# This is just temporary, as we will move these to a CLI tools
$ cd src/main/java/com/neko
$ javac UDPClient.java
$ java -cp . UDPClient "hello" "localhost"
```

# Check Your Coding Style

```Shell
$ gradle checkstyleMain
$ gradle checkstyleTest
```