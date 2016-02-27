package com.neko.monitor;

public interface NekoCallback {
    void invoke(String path, String text, String error);

    boolean isValid();
}
