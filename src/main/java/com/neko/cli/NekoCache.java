package com.neko.cli;

import static org.apache.commons.io.FileUtils.readFileToString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.logging.Logger;


class NekoCache {
    private static final Logger log = Logger.getLogger(NekoCache.class.getName());

    private static final String CACHE_DATA_FILE = ".cache_data.json";

    HashMap<String, FileMetadata> caches;

    private String cacheDirectory;

    public NekoCache() {
    }

    NekoCache(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
        loadCacheData();
    }

    private void loadCacheData() {
        String cacheDataFilePath = FilenameUtils.normalize(cacheDirectory + "/" + CACHE_DATA_FILE);
        File cacheDataFile = new File(cacheDataFilePath);
        if (!cacheDataFile.exists()) {
            caches = new HashMap<>();
            return;
        }

        try {
            String json = readFileToString(cacheDataFile);

            Gson gson = new Gson();

            Type hashMapType = new TypeToken<HashMap<String, FileMetadata>>(){}.getType();
            caches = gson.fromJson(json, hashMapType);
        } catch (IOException exception) {
            log.warning(exception.getMessage());
            caches = new HashMap<>();
        }
    }

    void save(String filePath,
                     long lastModified,
                     long lastValidation,
                     String text) {
        FileMetadata metadata = new FileMetadata();
        metadata.setLastValidation(lastValidation);
        metadata.setLastModified(lastModified);

        caches.put(filePath, metadata);

        log.fine("saving to cache");
        String fullFilPath = FilenameUtils.normalize(cacheDirectory + "/" + filePath);
        File cacheFile = new File(fullFilPath);
        try {
            FileUtils.write(cacheFile, text);

            Gson gson = new Gson();
            String json = gson.toJson(caches);

            File cacheDataFile = new File(cacheDirectory + "/" + CACHE_DATA_FILE);
            FileUtils.write(cacheDataFile, json);
        } catch (IOException exception) {
            log.warning(exception.getMessage());
        }
    }

    void remove(String filePath) {

    }

    FileMetadata readMetadata(String filePath) {
        return caches.get(filePath);
    }

    String read(String filePath, int offset, int length) throws IOException {
        if (!exist(filePath)) {
            return null;
        }

        File cacheFile = new File(cacheDirectory + "/" + filePath);
        String text = FileUtils.readFileToString(cacheFile);
        return text.substring(offset, offset + length);
    }

    boolean exist(String filePath) {
        return caches.containsKey(filePath);
    }
}
