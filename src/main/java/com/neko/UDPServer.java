package com.neko;

import com.neko.msg.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.channels.FileChannel;

import static com.neko.msg.NekoOpcode.*;

public class UDPServer {

    public static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1000;
    private static final String COPY_POSTFIX = "_copy";

    private static NekoData handleRead(String path, Integer offset, Integer length) {
        NekoData res = new NekoData();
        RandomAccessFile raf = null;
        byte[] inputBuffer = new byte[length];
        try {
            raf = new RandomAccessFile(path, "r");
            raf.seek(offset);
            raf.read(inputBuffer);
        } catch (FileNotFoundException e) {
            String errorMessage = "Unable to open file '" + path + "'";
            System.out.println(errorMessage);
            res.setOpcode(ERROR);
            res.setError(errorMessage);
            return res;
        } catch (IOException e) {
            String errorMessage = "Error reading file '" + path + "'";
            System.out.println(errorMessage);
            res.setOpcode(ERROR);
            res.setError(errorMessage);
            return res;
        } finally {
            try {
                if (raf != null) raf.close();
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
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            raf.seek(offset);
            raf.writeChars(text);
        } catch (FileNotFoundException e) {
            String errorMessage = "Unable to open file '" + path + "'";
            System.out.println(errorMessage);
            res.setOpcode(ERROR);
            res.setError(errorMessage);
            return res;
        } catch (IOException e) {
            String errorMessage = "Error writing file '" + path + "'";
            System.out.println(errorMessage);
            res.setOpcode(ERROR);
            res.setError(errorMessage);
            return res;
        } finally {
            try {
                if (raf != null) raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        res.setOpcode(RESULT);
        return res;
    }

    private static NekoData handleMonitor(String path, Integer interval) {
        NekoData res = new NekoData();
        res.setOpcode(RESULT);
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

        try {
            String copyPath = getCopyPath(path);
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
            }
            finally {
                if(source != null) {
                    source.close();
                }
                if(destination != null) {
                    destination.close();
                }
            }
        } catch (IOException e) {
            res.setOpcode(ERROR);
            String errorMessage = "Error writing file '" + path + "_copy'";
            System.out.println(errorMessage);
            res.setOpcode(ERROR);
            res.setError(errorMessage);
            return res;
        }
        res.setOpcode(RESULT);
        return res;
    }

    private static NekoData handleCount(String path) {
        NekoData res = new NekoData();
        res.setOpcode(RESULT);
        return res;
    }

    public static void main(String[] args) {
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

                NekoData r = deserializer.deserialize(requestPacket.getData());

                NekoData res = new NekoData();

                switch (r.getOpcode()) {
                    case READ:
                        res = handleRead(r.getPath(), r.getOffset(), r.getLength());
                        break;
                    case INSERT:
                        res = handleInsert(r.getPath(), r.getOffset(), r.getText());
                        break;
                    case MONITOR:
                        res = handleMonitor(r.getPath(), r.getInterval());
                        break;
                    case COPY:
                        res = handleCopy(r.getPath());
                        break;
                    case COUNT:
                        res = handleCount(r.getPath());
                        break;
                }

                byte[] resBytes = serializer.serialize(res).toBytes();

                DatagramPacket reply = new DatagramPacket(
                        resBytes,
                        resBytes.length,
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
