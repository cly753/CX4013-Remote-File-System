# Neko

Remote File System for CX4013 Distributed System

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
```