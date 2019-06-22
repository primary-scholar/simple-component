package com.mimu.simple.core;

import java.io.Serializable;

/**
 * author: mimu
 * date: 2018/10/27
 */
public class FileItem implements Serializable {
    private byte[] bytes;
    private String contentType;
    private String fileName;

    public FileItem(byte[] bytes,String contentType,String fileName){
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
