package com.geekbrains.common;

import java.io.*;
import java.nio.file.Path;

public class FileContainer implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;
    private String fileName;
    private byte[] data;
    private int part;
    private int ofParts;
    private long size;

    public FileContainer(Path path) {
        this.fileName = path.getFileName().toString();
        this.data = new byte[0];
        this.part = 0;
        this.ofParts = 1;
        this.size = 0;
    }

    public FileContainer() {
    }

    public long getSize() {
        return size;
    }
    public String getFileName() {
        return fileName;
    }
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setOfParts(int ofParts) {
        this.ofParts = ofParts;
    }

    public int getPart() {
        return part;
    }



    public int getOfParts() {
        return ofParts;
    }
}
