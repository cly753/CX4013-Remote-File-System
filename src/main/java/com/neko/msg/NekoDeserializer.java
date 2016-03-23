package com.neko.msg;

import java.util.InputMismatchException;

public class NekoDeserializer {

    public NekoDeserializer() {

    }

    public NekoData deserialize(byte[] bytes) {
        NekoData deserialized = new NekoData();

        NekoInputStream in = new NekoInputStream(bytes);
        NekoOpcode opcode = in.readOpcode();

        deserialized.setOpcode(opcode);

        while (in.hasNext()) {
            NekoAttribute attribute = in.readAttribute();

            //
            // TODO
            // any alternatives to switch ?
            // use map ?
            //
            switch (attribute) {
                case REQUEST_ID:
                    deserialized.setRequestId(in.readString());
                    break;
                case PATH:
                    deserialized.setPath(in.readString());
                    break;
                case OFFSET:
                    deserialized.setOffset(in.readInt());
                    break;
                case INTERVAL:
                    deserialized.setInterval(in.readInt());
                    break;
                case TEXT:
                    deserialized.setText(in.readString());
                    break;
                case LENGTH:
                    deserialized.setLength(in.readInt());
                    break;
                case NUMBER:
                    deserialized.setNumber(in.readInt());
                    break;
                case ERROR:
                    deserialized.setError(in.readString());
                    break;
                case LAST_MODIFIED:
                    deserialized.setLastModified(in.readString());
                    break;
                default:
                    throw new InputMismatchException("Unknown NekoAttribute");
            }
        }

        return deserialized;
    }
}
