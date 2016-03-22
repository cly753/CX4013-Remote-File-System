Neko: Remote File System for CX4013 Distributed System.

# Neko CLI

To use Neko CLI, we use the following command to generate Neko CLI binary:

```Shell
$ gradle installDist
```

Two files will be generated at `build/install/neko-file-system/bin/` folder, i.e. `neko-file-system` (for MAC OS
and Linux) and `neko-file-system.bat` (for Windows). We run the CLI by:

```Shell
$ cd build/install/neko-file-system/bin/

$ ./neko-file-system
# You should get the following output
# usage: neko [OPTIONS]
#  -d,--debug     print debug message
#  -h,--help      print this message
#  -v,--verbose   print verbose message
# 
# usage: neko read [ARGS] <path>
#  -b,--byte <arg>     the number of byte to be read
#  -d,--debug          print debug message
#  -o,--offset <arg>   read bytes starting from this offset
#  -v,--verbose        print verbose message
# 
# usage: neko insert [ARGS] <path>
#  -d,--debug          print debug message
#  -o,--offset <arg>   insert bytes starting from this offset
#     --text <arg>     textOption to be inserted
#  -v,--verbose        print verbose message
# 
# usage: neko monitor [ARGS] <path>
#  -d,--debug        print debug message
#     --time <arg>   time intervals in milliseconds
#  -v,--verbose      print verbose message
# 
# usage: neko copy [ARGS] <path>
#  -d,--debug     print debug message
#  -v,--verbose   print verbose message
# 
# usage: neko count [ARGS] <path>
#  -d,--debug     print debug message
#  -v,--verbose   print verbose message
```

Here are some examples:

```Shell
# Read 500 bytes, starting from the first byte, from the given file
$ ./neko-file-system read -b 500 -o 0 /home/andy/Documents/HelloWorld.txt

# Insert the string "hello world\n", where "\n" represents a new line character,
# starting from the first byte, to the given file
$ ./neko-file-system insert -o 0 --text "hello world\n" /home/andy/Documents/HelloWorld.txt

# Copy a file. Another file called <the-file-name>_copy.<the-extension> will be created.
# In the following example, HelloWorld_copy.txt will be created
$ ./neko-file-system copy /home/andy/Documents/HelloWorld.txt

# Count the number of files in the given path
$ ./neko-file-system count /home/andy/Documents
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