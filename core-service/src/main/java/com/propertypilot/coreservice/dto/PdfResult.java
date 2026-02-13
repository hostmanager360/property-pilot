package com.propertypilot.coreservice.dto;

public class PdfResult {
    private final byte[] bytes;
    private final String filename;

    public PdfResult(byte[] bytes, String filename) {
        this.bytes = bytes;
        this.filename = filename;
    }

    public byte[] getBytes() { return bytes; }
    public String getFilename() { return filename; }
}