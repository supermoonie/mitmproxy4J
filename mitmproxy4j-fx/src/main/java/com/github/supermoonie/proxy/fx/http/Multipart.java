package com.github.supermoonie.proxy.fx.http;

import com.github.supermoonie.proxy.fx.entity.Request;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2021/3/24
 */
public class Multipart {

    /**
     * HTTP content type header name.
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * HTTP content disposition header name.
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * HTTP content length header name.
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * Content-disposition value for form data.
     */
    public static final String FORM_DATA = "form-data";

    /**
     * Content-disposition value for file attachment.
     */
    public static final String ATTACHMENT = "attachment";

    /**
     * Part of HTTP content type header.
     */
    public static final String MULTIPART = "multipart/";

    /**
     * HTTP content type header for multiple uploads.
     */
    public static final String MULTIPART_MIXED = "multipart/mixed";

    /**
     * Tells if a request is multipart or not.
     *
     * @param request the javax.servlet.http.HttpServletRequest that we are going to check.
     * @return true if the request is multipart, false otherwise.
     */
    public static boolean isMultipartContent(Request request) {
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }

        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.toLowerCase().startsWith(MULTIPART);
    }


    public void parse(Request request, byte[] content, String charset, PartHandler partHandler) throws IOException {
        if (!isMultipartContent(request)) {
            throw new IllegalArgumentException("Not a multipart content. The HTTP method should be 'POST' and the " +
                    "Content-Type 'multipart/form-data' or 'multipart/mixed'.");
        }

        InputStream inputStream = new ByteArrayInputStream(content);
        String contentType = request.getContentType();

        byte[] boundary = getBoundary(contentType, charset);
        if (boundary == null) {
            throw new IllegalArgumentException("the request was rejected because no multipart boundary was found");
        }

        // create a multipart reader
        MultipartReader multipartReader = new MultipartReader(inputStream, boundary);
        multipartReader.setHeaderEncoding(charset);

        String currentFieldName = null;
        for (; ; ) {
            boolean nextPart;
            nextPart = multipartReader.skipPreamble();
            if (!nextPart) {
                if (currentFieldName == null) {
                    // outer multipart terminated -> no more data
                    return;
                }
                // inner multipart terminated -> return to parsing the outer
                multipartReader.setBoundary(boundary);
                currentFieldName = null;
                continue;
            }

            String headersString = multipartReader.readHeaders();
            Map<String, String> headers = getHeadersMap(headersString);

            if (currentFieldName == null) {

                // we're parsing the outer multipart
                String fieldName = getFieldName(headers.get(CONTENT_DISPOSITION));
                if (fieldName != null) {

                    String partContentType = headers.get(CONTENT_TYPE);
                    if (partContentType != null && partContentType.toLowerCase().startsWith(MULTIPART_MIXED)) {

                        // multiple files associated with this field name
                        currentFieldName = fieldName;
                        multipartReader.setBoundary(getBoundary(partContentType, charset));

                        continue;
                    }

                    String fileName = getFileName(headers.get(CONTENT_DISPOSITION));
                    if (fileName == null) {
                        // call the part handler
                        String value = Streams.asString(multipartReader.newInputStream());
                        partHandler.handleFormItem(fieldName, value);
                    } else {

                        // create the temp file
                        File tempFile = createTempFile(multipartReader);

                        // call the part handler
                        FileItem fileItem = new FileItem(fieldName, fileName, partContentType, tempFile.length(), tempFile, headers);
                        partHandler.handleFileItem(fieldName, fileItem);
                    }

                    continue;
                }
            } else {
                String fileName = getFileName(headers.get(CONTENT_DISPOSITION));
                String partContentType = headers.get(CONTENT_TYPE);
                if (fileName != null) {

                    // create the temp file
                    File tempFile = createTempFile(multipartReader);

                    // call the part handler
                    FileItem fileItem = new FileItem(currentFieldName, fileName, partContentType, tempFile.length(),
                            tempFile, headers);
                    partHandler.handleFileItem(currentFieldName, fileItem);
                    continue;
                }
            }
            multipartReader.discardBodyData();
        }

    }

    private File createTempFile(MultipartReader multipartReader) throws IOException {
        File tempFile = File.createTempFile("com.github.supermoonie.fx.proxy.http.file_", null);
        tempFile.deleteOnExit();
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            copy(multipartReader.newInputStream(), outputStream);
        }

        return tempFile;
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    private void copy(InputStream input, OutputStream output) throws IOException {
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    /**
     * Retreives a map with the headers of a part.
     *
     * @param headerPart a String object with the contents of the part header.
     * @return a Map<String,String> object with the headers of the part.
     */
    protected Map<String, String> getHeadersMap(String headerPart) {
        final int len = headerPart.length();
        final Map<String, String> headers = new HashMap<>(8);

        int start = 0;
        for (; ; ) {
            int end = parseEndOfLine(headerPart, start);
            if (start == end) {
                break;
            }
            StringBuilder header = new StringBuilder(headerPart.substring(start, end));
            start = end + 2;
            while (start < len) {
                int nonWs = start;
                while (nonWs < len) {
                    char c = headerPart.charAt(nonWs);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                    ++nonWs;
                }
                if (nonWs == start) {
                    break;
                }
                // continuation line found
                end = parseEndOfLine(headerPart, nonWs);
                header.append(" ").append(headerPart, nonWs, end);
                start = end + 2;
            }

            // parse header line
            final int colonOffset = header.toString().indexOf(':');
            if (colonOffset == -1) {
                // this header line is malformed, skip it.
                continue;
            }
            String headerName = header.substring(0, colonOffset).trim();
            String headerValue = header.substring(header.toString().indexOf(':') + 1).trim();

            if (headers.containsKey(headerName)) {
                headers.put(headerName, headers.get(headerName) + "," + headerValue);
            } else {
                headers.put(headerName, headerValue);
            }
        }

        return headers;
    }

    /**
     * Skips bytes until the end of the current line.
     *
     * @param headerPart the headers, which are being parsed.
     * @param end        index of the last byte, which has yet been processed.
     * @return index of the \r\n sequence, which indicates end of line.
     */
    private int parseEndOfLine(String headerPart, int end) {
        int index = end;
        for (; ; ) {
            int offset = headerPart.indexOf('\r', index);
            if (offset == -1 || offset + 1 >= headerPart.length()) {
                throw new IllegalStateException("Expected headers to be terminated by an empty line.");
            }
            if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            }
            index = offset + 1;
        }
    }

    /**
     * Retrieves the name of the field from the Content-Disposition header of the part.
     *
     * @param contentDisposition the value of the Content-Disposition header.
     * @return a String object that holds the name of the field to which this part is associated.
     */
    private String getFieldName(String contentDisposition) {
        String fieldName = null;

        if (contentDisposition != null && contentDisposition.toLowerCase().startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);

            // parameter parser can handle null input
            Map<String, String> params = parser.parse(contentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }

        return fieldName;
    }

    /**
     * Retrieves the boundary that is used to separate the request parts from the Content-Type header.
     *
     * @param contentType the value of the Content-Type header.
     * @return a byte array with the boundary.
     */
    protected byte[] getBoundary(String contentType, String charset) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = params.get("boundary");

        if (boundaryStr == null) {
            return null;
        }

        byte[] boundary;
        try {
            boundary = boundaryStr.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            boundary = boundaryStr.getBytes();
        }
        return boundary;
    }

    /**
     * Retrieves the file name of a file from the filename attribute of the Content-Disposition header of the part.
     *
     * @param contentDisposition the value of the Content-Disposition header.
     * @return a String object that holds the name of the file.
     */
    private String getFileName(String contentDisposition) {
        String fileName = null;
        if (contentDisposition != null) {
            String cdl = contentDisposition.toLowerCase();

            if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {

                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);

                // parameter parser can handle null input
                Map<String, String> params = parser.parse(contentDisposition, ';');
                if (params.containsKey("filename")) {
                    fileName = params.get("filename");
                    if (fileName != null) {
                        fileName = fileName.trim();
                    } else {
                        // even if there is no value, the parameter is present,
                        // so we return an empty file name rather than no file
                        // name.
                        fileName = "";
                    }
                }
            }
        }

        return fileName;
    }
}
