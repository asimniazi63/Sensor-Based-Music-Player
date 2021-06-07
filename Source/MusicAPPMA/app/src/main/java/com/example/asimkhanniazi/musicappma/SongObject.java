package com.example.asimkhanniazi.musicappma;

public class SongObject {

    String fileName;
    String absolutePath;

    public SongObject(String fileName,String absolutePath) {
        this.fileName = fileName;
        this.absolutePath=absolutePath;
    }

    public String getFileName() {

        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
