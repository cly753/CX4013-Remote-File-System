package com.neko.cli;

import java.util.HashMap;

/**
 * Created by andyccs on 23/3/16.
 */
public class NekoCache {
    private static final String CACHE_DATA_FILE = ".cache_data.json";

    private HashMap<String, FileMetadata> caches = new HashMap<>();

    public NekoCache(String cacheDirectory) {
    }

    public void loadCacheData() {

    }

    public void save(String filePath,
                     long lastModified,
                     long lastValidation,
                     String text) {

    }

    public void remove(String filePath) {

    }

    public FileMetadata readMetadata(String filePath) {
        return null;
    }

    public String read(String filePath, int offset, int length) {
        return "";
    }

    public boolean exist(String filePath) {
        return false;
    }
}
