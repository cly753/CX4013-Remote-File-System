package com.neko.cli;

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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Neko {

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

    private static Option help = new Option("help", "print this message");

    private static Options options = new Options();
    private static Options readOptions = new Options();
    private static Options insertOptions = new Options();
    private static Options monitorOptions = new Options();

    public static void main(String[] args) {
        options.addOption(help);

        readOptions.addOption(readOffsetOption);
        readOptions.addOption(byteOption);

        insertOptions.addOption(insertOffsetOption);
        insertOptions.addOption(textOption);

        monitorOptions.addOption(timeOption);

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
                String copyFilePath = getFilePath(commandArgs);
                System.out.println("file path: " + copyFilePath);
                // TODO(andyccs): copy logic here
                break;
            case "count":
                String countFilePath = getFilePath(commandArgs);
                System.out.println("file path: " + countFilePath);
                // TODO(andyccs): count logic here
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

            String filePath = getFilePath(line.getArgs());

            System.out.println("file path: " + filePath);
            System.out.println("offset: " + Integer.parseInt(line.getOptionValue("o")));
            System.out.println("byte: " + Integer.parseInt(line.getOptionValue("b")));

            NekoData request = new NekoData();
            request.setOpcode(READ);
            request.setPath(filePath);
            request.setOffset(Integer.parseInt(line.getOptionValue("o")));
            request.setLength(Integer.parseInt(line.getOptionValue("b")));

            NekoSerializer serializer = new NekoSerializer();
            byte[] requestBytes = serializer.serialize(request).toBytes();
            String respond = sendBytes(requestBytes);
            System.out.println(respond);
        } catch (ParseException exception) {
            System.err.println("Error: " + exception.getMessage());
            showHelps(readOptions, "read");
            System.exit(-1);
        } catch (IOException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.exit(-1);
        }
    }

    private static void insert(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(insertOptions, commandArgs);

            String filePath = getFilePath(line.getArgs());

            // TODO(andyccs): insert logic here
            System.out.println("file path: " + filePath);
            System.out.println("offset: " + Integer.parseInt(line.getOptionValue("o")));
            System.out.println("text: " + line.getOptionValue("text"));

        } catch (ParseException exp) {
            System.err.println("Error: " + exp.getMessage());
            showHelps(insertOptions, "insert");
            System.exit(-1);
        }
    }

    private static void monitor(String[] commandArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(monitorOptions, commandArgs);

            String filePath = getFilePath(line.getArgs());

            // TODO(andyccs): monitor logic here
            System.out.println("file path: " + filePath);
            System.out.println("time: " + Integer.parseInt(line.getOptionValue("time")));

        } catch (ParseException exp) {
            System.err.println("Error: " + exp.getMessage());
            showHelps(monitorOptions, "monitor");
            System.exit(-1);
        }
    }

    private static String getFilePath(String[] commandArgs) {
        if (commandArgs.length == 0) {
            System.err.println("Please provide a file path");
            System.exit(-1);
        }
        return commandArgs[0];
    }

    private static void showHelps() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("neko [OPTIONS]...", options);
        System.out.println("");
        showHelps(readOptions, "read");
        System.out.println("");
        showHelps(insertOptions, "insert");
        System.out.println("");
        showHelps(monitorOptions, "monitor");
        System.out.println("");
        showHelpCopy();
        System.out.println("");
        showHelpCount();
        System.out.println("");
    }

    private static void showHelps(Options options, String command) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("neko " + command + " [ARGS]", options);
    }

    private static void showHelpCopy() {
        System.out.println("usage: neko copy <path>");
    }

    private static void showHelpCount() {
        System.out.println("usage: neko count <path>");
    }

    private static String hostname = "localhost";
    private static int port = 6789;
    public static final int SOCKET_SIZE = 2244;
    public static final int BUFFER_SIZE = 1000;

    private static String sendBytes(byte[] requestBytes) throws IOException {

        InetAddress host = InetAddress.getByName(hostname);
        DatagramSocket socket = new DatagramSocket(SOCKET_SIZE);
        DatagramPacket requestPacket =
                new DatagramPacket(requestBytes, requestBytes.length, host, port);
        socket.send(requestPacket); //send packet using socket method

        byte[] buffer = new byte[BUFFER_SIZE]; //a buffer for receive
        DatagramPacket replyPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(replyPacket);

        NekoDeserializer deserializer = new NekoDeserializer();
        NekoData reply = deserializer.deserialize(replyPacket.getData());
        return reply.toString();
    }
}
