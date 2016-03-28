package com.neko;

import static com.neko.msg.NekoOpcode.ERROR;
import static com.neko.msg.NekoOpcode.RESULT;

import com.neko.monitor.NekoCallbackClient;
import com.neko.monitor.NekoCallbackClientTracker;
import com.neko.msg.NekoData;
import com.neko.msg.NekoDeserializer;
import com.neko.msg.NekoSerializer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class UDPServer {

    public static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1000;
    public static final int MONITOR_CLIENT_PORT = 8888;

    private static final String COPY_POSTFIX = "_copy";
    private static boolean AT_MOST_ONE = true; //true for AT_MOST_ONE, false for AT_LEAST_ONE
    private static HashMap<String, NekoData> history = new HashMap<>();

    private static NekoCallbackClientTracker callbackClientTracker = new NekoCallbackClientTracker();

    private static NekoData handleFileNotFound(String path) {
        NekoData res = new NekoData();
        String errorMessage = "The file does not exists: '" + path + "'";
        System.out.println(errorMessage);
        res.setOpcode(ERROR);
        res.setError(errorMessage);
        return res;
    }

    private static NekoData handleOffsetExceedsFileLength() {
        NekoData res = new NekoData();
        String errorMessage = "The specified offset exceeds the file length";
        System.out.println(errorMessage);
        res.setOpcode(ERROR);
        res.setError(errorMessage);
        return res;
    }

    private static NekoData handleIOException(String path, String op) {
        NekoData res = new NekoData();
        String errorMessage = "Error " + op + " file: '" + path + "'";
        System.out.println(errorMessage);
        res.setOpcode(ERROR);
        res.setError(errorMessage);
        return res;
    }

    private static NekoData handleRead(String path, Integer offset, Integer length) {
        NekoData res = new NekoData();
        RandomAccessFile raf = null;
        byte[] inputBuffer = new byte[length];
        try {
            raf = new RandomAccessFile(path, "r");
            long fileLength = raf.length();
            if (offset > fileLength) {
                res = handleOffsetExceedsFileLength();
                return res;
            }
            raf.seek(offset);
            raf.read(inputBuffer);
        } catch (FileNotFoundException e) {
            res = handleFileNotFound(path);
            return res;
        } catch (IOException e) {
            res = handleIOException(path, "reading");
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
        long fileLength = file.length();
        if (offset > fileLength) {
            res = handleOffsetExceedsFileLength();
            return res;
        }
        FileInputStream fis;
        FileOutputStream fos = null;
        byte[] data = new byte[(int) file.length()];

        //read
        try {
            fis = new FileInputStream(file);
            fis.read(data);
        } catch (FileNotFoundException e) {
            res = handleFileNotFound(path);
            return res;
        } catch (IOException e) {
            res = handleIOException(path, "writing");
            return res;
        }

        //modify
        String newText;
        try {
            String oldtext = new String(data, "UTF-8");
            if (offset == 0) {
                newText = text + oldtext;
            } else {
                newText = oldtext.substring(0, offset) + text + oldtext.substring(offset);
            }
        } catch (UnsupportedEncodingException e) {
            String errorMessage = "The file does not support UTF-8 encoding: '" + path + "'";
            System.out.print(errorMessage);
            res.setOpcode(ERROR);
            res.setError(errorMessage);
            return res;
        }

        //write
        try{
            fos = new FileOutputStream(file, false); // false to overwrite.
            fos.write(newText.getBytes());
            callbackClientTracker.informUpdate(path, null);
        } catch (FileNotFoundException e) {
            res = handleFileNotFound(path);
            return res;
        } catch (IOException e) {
            res = handleIOException(path, "writing");
            return res;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        res.setOpcode(RESULT);
        res.setNumber(text.length());
        return res;
    }

    private static NekoData handleMonitor(InetAddress address, String path, Integer interval) {
        NekoData res = new NekoData();
        res.setOpcode(RESULT);

        try {
            NekoCallbackClient client = new NekoCallbackClient(address, MONITOR_CLIENT_PORT, interval);
            callbackClientTracker.register(path, client);

            // TODO
            // set response to OK
            res.setError(null);
        } catch (UnknownHostException e) {
            e.printStackTrace();

            // TODO
            // set response to ERROR
            res.setError("Unknown Host");
        }
        return res;
    }

    private static String getCopyPath(String path) {
        int p = path.lastIndexOf('.');
        if (p == -1) {
            return path + COPY_POSTFIX;
        }
        return path.substring(0,p) + COPY_POSTFIX + path.substring(p);
    }

    private static NekoData handleCopy(String path) {
        NekoData res = new NekoData();

        File sourceFile = new File(path);

        //check if the deskFile exsits or not

        String copyPath = "";

        try {
            copyPath = getCopyPath(path);
            File destFile = new File(copyPath);
            while(destFile.exists()) {
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

    public static void main(String[] args) {
        if (args[0] == "1") {
            AT_MOST_ONE = true;
        } else {
            AT_MOST_ONE = false;
        }
        DatagramSocket socket = null;
        try {
            //bound to host and port
            socket = new DatagramSocket(SERVER_PORT);
            NekoDeserializer deserializer = new NekoDeserializer();
            NekoSerializer serializer = new NekoSerializer();

            //a buffer for receive
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                System.out.println("Request: " + new String(requestPacket.getData()));

                NekoData request = deserializer.deserialize(requestPacket.getData());

                NekoData respond = new NekoData();

                if (AT_MOST_ONE && history.containsKey(request.getRequestId())) {
                    respond = history.get(request.getRequestId());
                } else {
                    switch (request.getOpcode()) {
                        case READ:
                            respond = handleRead(request.getPath(),
                                    request.getOffset(),
                                    request.getLength());
                            break;
                        case INSERT:
                            respond = handleInsert(request.getPath(),
                                    request.getOffset(),
                                    request.getText());
                            break;
                        case MONITOR:
                            respond = handleMonitor(requestPacket.getAddress(), request.getPath(), request.getInterval());
                            break;
                        case COPY:
                            respond = handleCopy(request.getPath());
                            break;
                        case COUNT:
                            respond = handleCount(request.getPath());
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
