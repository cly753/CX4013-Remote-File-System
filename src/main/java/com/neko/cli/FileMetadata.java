package com.neko.cli;

class FileMetadata {
    private Long lastModified;
    private Long lastValidation;

    Long getLastModified() {
        return lastModified;
    }

    void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    Long getLastValidation() {
        return lastValidation;
    }

    void setLastValidation(Long lastValidation) {
        this.lastValidation = lastValidation;
    }
}
