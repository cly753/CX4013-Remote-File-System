package com.neko.cli;

/**
 * Created by andyccs on 23/3/16.
 */
public class FileMetadata {
    private Long lastModified;
    private Long lastValidation;

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Long getLastValidation() {
        return lastValidation;
    }

    public void setLastValidation(Long lastValidation) {
        this.lastValidation = lastValidation;
    }
}
