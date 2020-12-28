package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.mime.MimeMappings;
import com.github.supermoonie.proxy.util.ResponseUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class LocalMapIntercept implements RequestIntercept, ResponseIntercept {

    private final Logger log = LoggerFactory.getLogger(LocalMapIntercept.class);

    private boolean localMapFlag = false;
    private final Map<String, String> localMap = new HashMap<>();

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        String uri = ctx.getConnectionInfo().getUrl();
        if (localMapFlag) {
            String localUri = localMap.get(uri);
            if (null != localUri) {
                File file = new File(localUri);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        try {
                            String path = new URI(uri).getPath();
                            int index = path.lastIndexOf("/");
                            if (-1 == index) {
                                return ResponseUtils.htmlResponse("Not Found!", HttpResponseStatus.NOT_FOUND);
                            }
                            String fileName = path.substring(index + 1);
                            file = FileUtils.listFiles(file, null, false).stream()
                                    .filter(f -> f.isFile() && (f.getName().equals(fileName) || f.getName().startsWith(fileName + ".")))
                                    .findFirst().orElseThrow(() -> new FileNotFoundException("Not Found!"));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            return ResponseUtils.htmlResponse("Error: " + e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    String mimeType = "application/octet-stream";
                    String extension = FilenameUtils.getExtension(file.getName());
                    if (!"".equals(extension)) {
                        mimeType = MimeMappings.DEFAULT.get(extension);
                    }
                    try {
                        ByteBuf content = Unpooled.wrappedBuffer(FileUtils.readFileToByteArray(file));
                        FullHttpResponse response =
                                new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content);
                        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeType);
                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                        return response;
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return ResponseUtils.htmlResponse("Error: " + e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return ResponseUtils.htmlResponse("Not Found!", HttpResponseStatus.NOT_FOUND);
                }
            }
        }
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        return null;
    }

    public boolean isLocalMapFlag() {
        return localMapFlag;
    }

    public void setLocalMapFlag(boolean localMapFlag) {
        this.localMapFlag = localMapFlag;
    }

    public Map<String, String> getLocalMap() {
        return localMap;
    }
}
