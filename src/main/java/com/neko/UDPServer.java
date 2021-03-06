package com.neko;

import static com.neko.msg.NekoOpcode.ERROR;
import static com.neko.msg.NekoOpcode.LAST_MODIFIED;
import static com.neko.msg.NekoOpcode.RESULT;
import static org.apache.commons.io.FileUtils.readFileToString;

import com.neko.monitor.NekoCallbackClient;
import com.neko.monitor.NekoCallbackClientTracker;
import com.neko.msg.NekoData;
import com.neko.msg.NekoDeserializer;
import com.neko.msg.NekoSerializer;
import com.neko.simulation.UnstableDatagramSocket;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer {
    private static final Logger log = Logger.getLogger(UDPServer.class.getName());

    private static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1000;
    private static final int MONITOR_CLIENT_PORT = 8888;

    private static final String COPY_POSTFIX = "_copy";
    private static HashMap<String, NekoData> history = new HashMap<>();

    private static NekoCallbackClientTracker callbackClientTracker =
            new NekoCallbackClientTracker();

    private static NekoData handleRead(String path) {
        File file = new File(path);
        String text;
        try {
            text = FileUtils.readFileToString(file);
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());

            NekoData errorRespond = new NekoData();
            errorRespond.setOpcode(ERROR);
            errorRespond.setError(e.getMessage());
            return errorRespond;
        }
        NekoData respond = new NekoData();
        respond.setOpcode(RESULT);
        respond.setText(text);
        respond.setLastModified(String.valueOf(file.lastModified()));
        return respond;
    }

    private static NekoData handleRead(String path, Integer offset, Integer length) {
        NekoData res = new NekoData();
        RandomAccessFile raf = null;
        byte[] inputBuffer = new byte[length];
        try {
            raf = new RandomAccessFile(path, "r");
            raf.seek(offset);
            raf.read(inputBuffer);
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, e.getMessage());
            res.setOpcode(ERROR);
            res.setError(e.getMessage());
            return res;
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
            res.setOpcode(ERROR);
            res.setError(e.getMessage());
            return res;
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        res.setOpcode(RESULT);
        res.setText(new String(inputBuffer));
        return res;
    }

    private static NekoData handleInsert(String path, Integer offset, String text) {
        NekoData res = new NekoData();

        File file = new File(path);
        try {
            String oldtext = readFileToString(file);
            String newText = oldtext.substring(0, offset) + text + oldtext.substring(offset);
            FileUtils.writeStringToFile(file, newText);

            callbackClientTracker.informUpdate(path, null);
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
            res.setOpcode(ERROR);
            res.setError(e.getMessage());
            return res;
        }
        res.setOpcode(RESULT);
        return res;
    }

    private static NekoData handleMonitor(InetAddress address, String path, Integer interval) {
        NekoData res = new NekoData();


        try {
            long validUntil = System.currentTimeMillis() + interval;
            NekoCallbackClient client =
                    new NekoCallbackClient(address, MONITOR_CLIENT_PORT, validUntil);
            callbackClientTracker.register(path, client);

            // TODO
            // set response to OK
        } catch (UnknownHostException e) {
            e.printStackTrace();

            // TODO
            // set response to ERROR
            res.setOpcode(ERROR);
            res.setError("Unknown Host");
            return res;
        }
        res.setOpcode(RESULT);
        return res;
    }

    private static String getCopyPath(String path) {
        int position = path.lastIndexOf('.');
        if (position == -1) {
            return path + COPY_POSTFIX;
        }
        return path.substring(0, position) + COPY_POSTFIX + path.substring(position);
    }

    private static NekoData handleCopy(String path) {
        NekoData res = new NekoData();

        File sourceFile = new File(path);

        //check if the deskFile exsits or not

        String copyPath = "";

        try {
            copyPath = getCopyPath(path);
            File destFile = new File(copyPath);
            while (destFile.exists()) {
                copyPath = getCopyPath(copyPath);
                destFile = new File(copyPath);
            }
            destFile.createNewFile();

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } catch (IOException e) {
            res.setOpcode(ERROR);
            String errorMessage = "Error writing file '" + copyPath + "'";
            System.out.println(errorMessage);
            res.setError(errorMessage);
            return res;
        }
        res.setOpcode(RESULT);
        return res;
    }

    private static NekoData handleCount(String path) {
        File file = new File(path);
        NekoData res = new NekoData();
        if (!file.exists()) {
            // return error message that the file does not exists
            res.setOpcode(ERROR);
            String errorMessage = path + " does not exists";
            System.out.println(errorMessage);
            res.setError(errorMessage);
            return res;
        }
        if (!file.isDirectory()) {
            res.setOpcode(ERROR);
            String errorMessage = path + " is not a directory";
            System.out.println(errorMessage);
            res.setError(errorMessage);
            return res;
        }
        res.setOpcode(RESULT);
        int numberOfFiles = file.listFiles().length;
        res.setNumber(numberOfFiles);
        return res;
    }

    private static NekoData handleLastModified(String path) {
        File file = new File(path);
        NekoData respond = new NekoData();
        if (!file.exists()) {
            respond.setOpcode(ERROR);
            String errorMessage = path + " does not exists";
            System.out.println(errorMessage);
            respond.setError(errorMessage);
            return respond;
        }

        respond.setOpcode(LAST_MODIFIED);
        respond.setLastModified(String.valueOf(file.lastModified()));
        return respond;
    }

    public static void main(String[] args) {
        // If the first argument is "1", then we use at-most-once invocation semantic
        // Else, we use at-least-once invocation semantic (default)
        boolean atMostOnce = args.length > 0 && args[0].equals("1");
        log.info("at most once: " + atMostOnce);

        // If the second argument is "1" and "2",
        // then we use unstable datagram socket for simulation.
        // Else, we use normal datagram socket (default)
        //
        // "1": reply packets from server will lost for 3 times
        // "2": request packets from client will lost for 3 times
        String unstable = args.length > 1 ? args[1] : "";
        log.info("unstable datagram: " + unstable);

        DatagramSocket socket = null;
        try {
            //bound to host and port
            if (unstable.equals("1")) {
                // The server will always receive the request packet, but
                // will drop the first three reply packet.
                socket = new UnstableDatagramSocket(SERVER_PORT, "1111111111", "0001111111");
            } else if (unstable.equals("2")) {
                socket = new UnstableDatagramSocket(SERVER_PORT, "0001111111", "1111111111");
            } else {
                socket = new DatagramSocket(SERVER_PORT);
            }
            NekoDeserializer deserializer = new NekoDeserializer();
            NekoSerializer serializer = new NekoSerializer();

            //a buffer for receive
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                System.out.println("Request: " + new String(requestPacket.getData()));

                NekoData request = deserializer.deserialize(requestPacket.getData());

                NekoData respond;

                String requestId = request.getRequestId();
                if (atMostOnce && history.containsKey(requestId)) {
                    respond = history.get(requestId);
                } else {
                    switch (request.getOpcode()) {
                        case READ:
                            if (request.getOffset() == null && request.getLength() == null) {
                                respond = handleRead(request.getPath());
                            } else {
                                respond = handleRead(request.getPath(),
                                        request.getOffset(),
                                        request.getLength());
                            }
                            break;
                        case INSERT:
                            respond = handleInsert(request.getPath(),
                                    request.getOffset(),
                                    request.getText());
                            break;
                        case MONITOR:
                            respond = handleMonitor(requestPacket.getAddress(),
                                    request.getPath(),
                                    request.getInterval());
                            break;
                        case COPY:
                            respond = handleCopy(request.getPath());
                            break;
                        case COUNT:
                            respond = handleCount(request.getPath());
                            break;
                        case LAST_MODIFIED:
                            respond = handleLastModified(request.getPath());
                            break;
                        default:
                            // If the operation code is not defined, we just skip this request
                            continue;
                    }
                }

                byte[] respondBytes = serializer.serialize(respond).toBytes();

                DatagramPacket reply = new DatagramPacket(
                        respondBytes,
                        respondBytes.length,
                        requestPacket.getAddress(),
                        requestPacket.getPort());
                socket.send(reply); //send packet using socket method
                history.put(requestId, respond);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}