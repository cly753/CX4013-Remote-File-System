package com.neko.cli;

import static com.neko.msg.NekoOpcode.COPY;
import static com.neko.msg.NekoOpcode.COUNT;
import static com.neko.msg.NekoOpcode.INSERT;
import static com.neko.msg.NekoOpcode.LAST_MODIFIED;
import static com.neko.msg.NekoOpcode.MONITOR;
import static com.neko.msg.NekoOpcode.READ;

import com.neko.monitor.NekoCallback;
import com.neko.monitor.NekoCallbackServer;
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
import java.net.SocketException;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
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

    private static Option freshnessOption = OptionBuilder.withLongOpt("fresh")
            .withDescription("the freshness interval of cache")
            .hasArg()
            .withType(Long.class)
            .create();

    private static Option help = new Option("h", "help", false, "print this message");
    private static Option debug = new Option("d", "debug", false, "print debug message");
    private static Option verbose = new Option("v", "verbose", false, "print verbose message");
    private static Option portOption = OptionBuilder.withLongOpt("port")
            .withDescription("client datagram port")
            .hasArg()
            .withType(Integer.class)
            .create();

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
        readOptions.addOption(freshnessOption);
        readOptions.addOption(portOption);
        readOptions.addOption(debug);
        readOptions.addOption(verbose);

        insertOptions.addOption(insertOffsetOption);
        insertOptions.addOption(textOption);
        insertOptions.addOption(portOption);
        insertOptions.addOption(debug);
        insertOptions.addOption(verbose);

        monitorOptions.addOption(timeOption);
        monitorOptions.addOption(portOption);
        monitorOptions.addOption(debug);
        monitorOptions.addOption(verbose);

        copyOptions.addOption(portOption);
        copyOptions.addOption(debug);
        copyOptions.addOption(verbose);

        countOptions.addOption(portOption);
        countOptions.addOption(debug);
        countOptions.addOption(verbose);

        log.setLevel(Level.INFO);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);

        log.addHandler(consoleHandler);
    }

    private static final String REQUEST_ID_1 = String.valueOf(System.currentTimeMillis());
    private static final String REQUEST_ID_2 = REQUEST_ID_1 + 1;

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelps();
            System.exit(-1);
        }

        String command = args[0];

        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);

        while (true) {
            try {
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
            } catch (SocketException exception) {
                log.log(Level.WARNING, "Error: " + exception.getMessage());
                log.log(Level.INFO, "Resending command");
                continue;
            } catch (IOException exception) {
                log.log(Level.WARNING, "Error: " + exception.getMessage());
            }
            break;
        }
    }

    private static long freshnessInterval = 10000L;

    private static void read(String[] commandArgs) throws IOException {
        NekoCache cache = new NekoCache(".nekocache");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(readOptions, commandArgs);
            setLoggerLevel(line);
            setDatagramPort(line);

            if (line.hasOption(freshnessOption.getLongOpt())) {
                freshnessInterval = Long.parseLong(
                    line.getOptionValue(freshnessOption.getLongOpt()));
                log.fine("set freshness interval to " + freshnessInterval);
            }

            String filePath = getFilePath(line.getArgs());
            int offset = Integer.parseInt(line.getOptionValue("o"));
            int length = Integer.parseInt(line.getOptionValue("b"));

            log.fine("file path: " + filePath);
            log.fine("offset: " + offset);
            log.fine("byte: " + length);

            if (!cache.exist(filePath)) {
                log.fine("No cache available, read from server");
                NekoData respond1 = readFromServer(filePath, offset, length);
                cache.save(filePath,
                        Long.parseLong(respond1.getLastModified()),
                        System.currentTimeMillis(),
                        respond1.getText());
                return;
            }

            FileMetadata cachedFileMetadata = cache.readMetadata(filePath);
            if (System.currentTimeMillis()
                    - cachedFileMetadata.getLastValidation() < freshnessInterval) {
                log.fine("File still fresh, read from cache");

                String text = cache.read(filePath);
                log.info(nekoSubstring(offset, length, text));
                return;
            }

            log.fine("File not fresh, read last modified from server");
            NekoData request = new NekoData();
            request.setOpcode(LAST_MODIFIED);
            request.setRequestId(REQUEST_ID_2);
            request.setPath(filePath);
            NekoData respond = sendBytes(request);
            Long serverLastModified = Long.parseLong(respond.getLastModified());

            if (Objects.equals(cachedFileMetadata.getLastModified(), serverLastModified)) {
                log.fine("Server file not modified, read from cache");

                cachedFileMetadata.setLastValidation(System.currentTimeMillis());
                String text = cache.read(filePath);
                log.info(nekoSubstring(offset, length, text));
            } else {
                log.fine("Server file modified, read from server and cache");

                cache.remove(filePath);

                NekoData respond2 = readFromServer(filePath, offset, length);
                cache.save(filePath,
                        Long.parseLong(respond2.getLastModified()),
                        System.currentTimeMillis(),
                        respond2.getText());
            }
        } catch (ParseException exception) {
            log.warning("Error: " + exception.getMessage());
            showHelps(readOptions, "read");
            System.exit(-1);
        }
    }

    private static NekoData readFromServer(String filePath, int offset, int length)
        throws IOException {
        NekoData request = new NekoData();
        request.setOpcode(READ);
        request.setRequestId(REQUEST_ID_1);
        request.setPath(filePath);

        NekoData respond = sendBytes(request);
        log.fine(respond.toString());

        String fullText = respond.getText();
        log.info(nekoSubstring(offset, length, fullText));
        return respond;
    }

    private static String nekoSubstring(int offset, int length, String fullText) {
        return fullText.substring(offset, offset + length);
    }

    private static void insert(String[] commandArgs) throws IOException {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(insertOptions, commandArgs);
            setLoggerLevel(line);
            setDatagramPort(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.FINE, "file path: " + filePath);
            log.log(Level.FINE, "offset: " + Integer.parseInt(line.getOptionValue("o")));
            log.log(Level.FINE, "text: " + line.getOptionValue("text"));

            NekoData request = new NekoData();
            request.setOpcode(INSERT);
            request.setRequestId(REQUEST_ID_1);
            request.setPath(filePath);
            request.setOffset(Integer.parseInt(line.getOptionValue("o")));
            request.setText(StringEscapeUtils.unescapeJava(line.getOptionValue("text")));

            NekoData respond = sendBytes(request);
            if (respond.getError() == null) {
                log.log(Level.INFO, "copy done");
            } else {
                log.log(Level.INFO, "Error: " + respond.getError());
            }
            log.log(Level.FINE, respond.toString());
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(insertOptions, "insert");
            System.exit(-1);
        }
    }

    private static void monitor(String[] commandArgs) throws IOException {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(monitorOptions, commandArgs);
            setLoggerLevel(line);
            setDatagramPort(line);

            String filePath = getFilePath(line.getArgs());
            Integer timeInterval = Integer.parseInt(line.getOptionValue("time"));

            // TODO(andyccs): monitor logic here
            log.log(Level.FINE, "file path: " + filePath);
            log.log(Level.FINE, "time: " + timeInterval);

            NekoData request = new NekoData();
            request.setOpcode(MONITOR);
            request.setRequestId(REQUEST_ID_1);
            request.setPath(filePath);
            request.setInterval(timeInterval);

            NekoData respond = sendBytes(request);
            log.log(Level.FINE, "respond:" + respond.toString());

            log.log(Level.INFO, "Start listening for changes");
            NekoCallback callback = new NekoCallback() {
                @Override
                public void invoke(String path, String text, String error) {
                    log.log(Level.INFO, "The path is updated.");
                }

                @Override
                public boolean isValid() {
                    return true;
                }
            };
            NekoCallbackServer callbackServer = new NekoCallbackServer(8888, 5000, callback);
            callbackServer.start(timeInterval);

        } catch (ParseException exp) {
            log.log(Level.WARNING, "Error: " + exp.getMessage());
            showHelps(monitorOptions, "monitor");
            System.exit(-1);
        }
    }

    private static void copy(String[] commandArgs) throws IOException {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(copyOptions, commandArgs);
            setLoggerLevel(line);
            setDatagramPort(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.FINE, "file path: " + filePath);

            NekoData request = new NekoData();
            request.setRequestId(REQUEST_ID_1);
            request.setOpcode(COPY);
            request.setPath(filePath);

            NekoData respond = sendBytes(request);
            if (respond.getError() == null) {
                log.log(Level.INFO, "copy done");
            } else {
                log.log(Level.INFO, "Error: " + respond.getError());
            }
            log.log(Level.FINE, respond.toString());
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(copyOptions, "copy");
            System.exit(-1);
        }
    }

    private static void count(String[] commandArgs) throws IOException {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(countOptions, commandArgs);
            setLoggerLevel(line);
            setDatagramPort(line);

            String filePath = getFilePath(line.getArgs());

            log.log(Level.INFO, "file path: " + filePath);

            NekoData request = new NekoData();
            request.setRequestId(REQUEST_ID_1);
            request.setOpcode(COUNT);
            request.setPath(filePath);

            NekoData respond = sendBytes(request);
            log.log(Level.INFO, "count: " + respond.getNumber());
            log.log(Level.FINE, respond.toString());
        } catch (ParseException exception) {
            log.log(Level.WARNING, "Error: " + exception.getMessage());
            showHelps(countOptions, "count");
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
        if (line.hasOption(debug.getOpt())) {
            log.setLevel(Level.FINEST);
        } else if (line.hasOption(verbose.getOpt())) {
            log.setLevel(Level.ALL);
        }
    }

    private static void setDatagramPort(CommandLine line) {
        if (line.hasOption(portOption.getLongOpt())) {
            DATAGRAM_PORT = Integer.parseInt(line.getOptionValue(portOption.getLongOpt()));
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

    private static final String hostname = "localhost";
    private static final int port = 6789;
    private static int DATAGRAM_PORT = 2244;

    private static NekoData sendBytes(NekoData request) throws IOException {
        NekoSerializer serializer = new NekoSerializer();
        byte[] requestBytes = serializer.serialize(request).toBytes();

        InetAddress host = InetAddress.getByName(hostname);

        // Convert bytes to datagram socket
        DatagramPacket requestPacket =
                new DatagramPacket(requestBytes, requestBytes.length, host, port);

        // Send the datagram
        DatagramSocket socket = new DatagramSocket(DATAGRAM_PORT);
        socket.setSoTimeout(1000);
        socket.send(requestPacket);

        // Receive the respond datagram from server
        // TODO(andyccs): How to receive all respond bytes from server?
        byte[] buffer = new byte[5000];
        DatagramPacket replyPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(replyPacket);
        socket.close();

        // Deserialize the respond
        NekoDeserializer deserializer = new NekoDeserializer();
        return deserializer.deserialize(replyPacket.getData());
    }
}
