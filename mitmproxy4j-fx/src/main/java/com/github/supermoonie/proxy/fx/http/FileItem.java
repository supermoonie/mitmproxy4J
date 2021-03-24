package com.github.supermoonie.proxy.fx.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2021/3/24
 */
public class FileItem {

    /**
     * The name of the field to which this file was associated in the HTTP request
     */
    private String name;

    /**
     * The name of the file taken from the HTTP part (in the filename attribute of the Content-Disposition header)
     */
    private String fileName;

    /**
     * The content type of the file taken from the Content-Type header of the part, null if not specified
     */
    private String contentType;

    /**
     * The content length of the file taken from the Content-Length header of the part, -1 if not specified
     */
    private long contentLength;

    /**
     * The file.
     */
    private File file;

    /**
     * The headers of the file part
     */
    private Map<String,String> headers;

    /**
     * Constructor.
     *
     * @param fieldName the name of the field that holds the file.
     * @param fileName the name of the file.
     * @param contentType
     * @param contentLength
     * @param file
     * @param headers
     */
    public FileItem(String fieldName, String fileName, String contentType, long contentLength, File file, Map<String,String> headers) {
        Objects.requireNonNull(fieldName, "no fieldName provided.");
        Objects.requireNonNull(fileName, "no fileName provided.");
        Objects.requireNonNull(file, "no inputStream provided");

        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.file = file;
        this.headers = headers;
        if (headers == null) {
            this.headers = new HashMap<>();
        }
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public File getFile() {
        return file;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}
