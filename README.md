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

Monitor examples:

```Shell
# Run the following command in a terminal
# The command will monitor the specific file for 10 second at port 8888
$ ./neko-file-system monitor --time 10000 --debug /Users/andyccs/Documents/hello.txt

# Run the following command in another terminal
$ ./neko-file-system insert -o 0 --text "hello world\n" /Users/andyccs/Documents/hello.txt
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

# Simulation

Neko server can be run in two different invocation semantic, i.e. at-least-once invocation semantic and at-most-once invocation semantic. The `UDPServer.java` can accept two input arguments.

First argument is invocation semantic of server, possible values:

- "0": at-least-once invocation semantic (default)
- "1": at-most-once invocation semantic

Second argument is unstable mode, possible values:

- "0": stable
- "1": reply packets from server will lost for 3 times
- "2": request packets from client will lost for 3 times

# Merge all codes into one file

Why we want to do this? Well, as your lecture.

```Shell
# Copy from https://github.com/tanhauhau/Bueno4013
$ sh merge.sh
```

# Check Your Coding Style

```Shell
$ gradle checkstyleMain
$ gradle checkstyleTest
```