package com.neko.cli;

import static com.neko.msg.NekoOpcode.INSERT;
import static com.neko.msg.NekoOpcode.READ;

import com.neko.msg.NekoData;
import com.neko.msg.NekoDeserializer;
import com.neko.msg.NekoSerializer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Neko {
    private static final Logger log = Logger.getLogger(Neko.class.getName());

    private static Option readOffsetOption = OptionBuilder.withLongOpt("offset")
            .withDescription("read bytes starting from this offset")
            .isRequired()
            .hasArg()
            .withType(Integer.class)
            .create("o");

    private static Option insertOffsetOption = OptionBuilder.withLongOpt("offset")
            .withDescription("insert bytes starting from this offset")
            .isRequired()
            .hasArg()
            .withType(Integer.class)
            .create("o");

    private static Option byteOption = OptionBuilder.withLongOpt("byte")
            .withDescription("the number of byte to be read")
            .isRequired()
            .hasArg()
            .withType(Integer.class)
            .create("b");

    private static Option textOption = OptionBuilder.withLongOpt("text")
            .withDescription("textOption to be inserted")
            .isRequired()
            .hasArg()
            .withType(String.class)
            .create();

    private static Option timeOption = OptionBuilder.withLongOpt("time")
            .withDescription("time intervals in milliseconds")
            .isRequired()
            .hasArg()
            .withType(Integer.class)
            .create();

    private static Option help = new Option("h", "help", false, "print this message");
    private static Option debug = new Option("d", "debug", false, "print debug message");
    private static Option verbose = new Option("v", "verbose", false, "print verbose message");

    private static Options options = new Options();
    private static Options readOptions = new Options();
    private static Options insertOptions = new Options();
    private static Options monitorOptions = new Options();
    private static Options copyOptions = new Options();
    private static Options countOptions = new Options();

    static {
        options.addOption(help);
        options.addOption(debug);
        options.addOption(verbose);

        readOptions.addOption(readOffsetOption);
        readOptions.addOption(byteOption);
        readOptions.addOption(debug);
        readOptions.addOption(verbose);

        insertOptions.addOption(insertOffsetOption);
        insertOptions.addOption(textOption);
        insertOptions.addOption(debug);
        insertOptions.addOption(verbose);

        monitorOptions.addOption(timeOption);
        monitorOptions.addOption(debug);
        monitorOptions.addOption(verbose);

        copyOptions.addOption(debug);
        copyOptions.addOption(verbose);

        countOptions.addOption(debug);
        countOptions.addOption(verbose);

        log.setLevel(Level.WARNING);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelps();
            System.exit(-1);
        }

        String command = args[0];

        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);

        switch (command) {
            case "read":
                read(commandArgs);
                break;
            case "insert":
                insert(commandArgs);
                break;
            case "monitor":
                monitor(commandArgs);
                break;
            case "copy":
                copy(commandArgs);
                break;
            case "count":
                count(commandArgs);
                break;
            default:
                showHelps();
                break;
        }
    }

    private static void read(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(readOptions, commandArgs);
            setLoggerLevel(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.INFO, "file path: " + filePath);
            log.log(Level.INFO, "offset: " + Integer.parseInt(line.getOptionValue("o")));
            log.log(Level.INFO, "byte: " + Integer.parseInt(line.getOptionValue("b")));

            NekoData request = new NekoData();
            request.setOpcode(READ);
            request.setPath(filePath);
            request.setOffset(Integer.parseInt(line.getOptionValue("o")));
            request.setLength(Integer.parseInt(line.getOptionValue("b")));

            String respond = sendBytes(request);
            System.out.println(respond);
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(readOptions, "read");
            System.exit(-1);
        } catch (IOException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            System.exit(-1);
        }
    }

    private static void insert(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(insertOptions, commandArgs);
            setLoggerLevel(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.INFO, "file path: " + filePath);
            log.log(Level.INFO, "offset: " + Integer.parseInt(line.getOptionValue("o")));
            log.log(Level.INFO, "text: " + line.getOptionValue("text"));

            NekoData request = new NekoData();
            request.setOpcode(INSERT);
            request.setPath(filePath);
            request.setOffset(Integer.parseInt(line.getOptionValue("o")));
            request.setText(StringEscapeUtils.unescapeJava(line.getOptionValue("text")));

            String respond = sendBytes(request);
            System.out.println(respond);
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(insertOptions, "insert");
            System.exit(-1);
        } catch (IOException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            System.exit(-1);
        }
    }

    private static void monitor(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(monitorOptions, commandArgs);
            setLoggerLevel(line);

            String filePath = getFilePath(line.getArgs());

            // TODO(andyccs): monitor logic here
            log.log(Level.INFO, "file path: " + filePath);
            log.log(Level.INFO, "time: " + Integer.parseInt(line.getOptionValue("time")));

        } catch (ParseException exp) {
            log.log(Level.WARNING, "Error: " + exp.getMessage());
            showHelps(monitorOptions, "monitor");
            System.exit(-1);
        }
    }

    private static void copy(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(copyOptions, commandArgs);
            setLoggerLevel(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.INFO, "file path: " + filePath);

            // TODO(andyccs): copy logic here
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(monitorOptions, "copy");
            System.exit(-1);
        }
    }

    private static void count(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(countOptions, commandArgs);
            setLoggerLevel(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.INFO, "file path: " + filePath);

            // TODO(andyccs): count logic here
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(monitorOptions, "count");
            System.exit(-1);
        }
    }

    private static String getFilePath(String[] commandArgs) {
        if (commandArgs.length == 0) {
            log.log(Level.WARNING, "Please provide a file path");
            System.exit(-1);
        }
        return commandArgs[0];
    }

    private static void setLoggerLevel(CommandLine line) {
        if (line.hasOption("d")) {
            log.setLevel(Level.INFO);
        } else if (line.hasOption("v")) {
            log.setLevel(Level.ALL);
        }
    }

    private static void showHelps() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("neko [OPTIONS]", options);
        System.out.println("");
        showHelps(readOptions, "read");
        System.out.println("");
        showHelps(insertOptions, "insert");
        System.out.println("");
        showHelps(monitorOptions, "monitor");
        System.out.println("");
        showHelps(copyOptions, "copy");
        System.out.println("");
        showHelps(countOptions, "count");
        System.out.println("");
    }

    private static void showHelps(Options options, String command) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("neko " + command + " [ARGS] <path>", options);
    }

    private static String hostname = "localhost";
    private static int port = 6789;
    public static final int DATAGRAM_PORT = 2244;

    private static String sendBytes(NekoData request) throws IOException {
        NekoSerializer serializer = new NekoSerializer();
        byte[] requestBytes = serializer.serialize(request).toBytes();

        InetAddress host = InetAddress.getByName(hostname);

        // Convert bytes to datagram socket
        DatagramPacket requestPacket =
                new DatagramPacket(requestBytes, requestBytes.length, host, port);

        // Send the datagram
        DatagramSocket socket = new DatagramSocket(DATAGRAM_PORT);
        socket.send(requestPacket);

        // Receive the respond datagram from server
        // TODO(andyccs): How to receive all respond bytes from server?
        byte[] buffer = new byte[5000];
        DatagramPacket replyPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(replyPacket);

        // Deserialize the respond
        NekoDeserializer deserializer = new NekoDeserializer();
        NekoData reply = deserializer.deserialize(replyPacket.getData());
        return reply.toString();
    }
}
