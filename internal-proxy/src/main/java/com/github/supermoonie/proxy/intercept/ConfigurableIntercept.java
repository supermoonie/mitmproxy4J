package com.github.supermoonie.proxy.intercept;


import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.mime.MimeMappings;
import com.github.supermoonie.proxy.util.RequestUtils;
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
import java.util.*;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public class ConfigurableIntercept implements RequestIntercept, ResponseIntercept {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurableIntercept.class);

    private boolean blockFlag = false;
    private final Set<String> blockUriList = new HashSet<>();
    private boolean allowFlag = false;
    private final Set<String> allowUriList = new HashSet<>();
    private final List<String> useSecondProxyHostList = new ArrayList<>();
    private final List<String> notUseSecondProxyHostList = new ArrayList<>();
    private boolean remoteMapFlag = false;
    private final Map<String, String> remoteUriMap = new HashMap<>();
    private boolean localMapFlag = false;
    private final Map<String, String> localMap = new HashMap<>();

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        String uri = ctx.getConnectionInfo().getUrl();
        if (blockFlag) {
            for (String reg : blockUriList) {
                if (uri.equals(reg) || uri.matches(reg)) {
                    return ResponseUtils.htmlResponse("Blocked!", HttpResponseStatus.OK);
                }
            }
        }
        if (allowFlag) {
            boolean match = allowUriList.stream().anyMatch(allowUri -> uri.equals(allowUri) || uri.matches(allowUri));
            if (!match) {
                return ResponseUtils.htmlResponse("Not In Allow List!", HttpResponseStatus.OK);
            }
        }
        String host = request.headers().get(HttpHeaderNames.HOST);
        if (remoteMapFlag) {
            String remoteUri = remoteUriMap.get(uri);
            ConnectionInfo info = RequestUtils.parseUri(remoteUri);
            if (null != remoteUri && null != info) {
                request.setUri(remoteUri);
                request.headers().set(HttpHeaderNames.HOST, info.getRemoteHost());
                ConnectionInfo originInfo = ctx.getConnectionInfo();
                originInfo.setRemoteHost(info.getRemoteHost());
                originInfo.setRemotePort(info.getRemotePort());
                originInfo.setHostHeader(info.getRemoteHost() + ":" + info.getRemotePort());
            }
        }
        if (localMapFlag) {
            String localUri = localMap.get(uri);
            if (null != localUri) {
                File file = new File(localUri);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        try {
                            String path = new URI(uri).getPath();
                            int index = uri.lastIndexOf("/");
                            if (-1 == index) {
                                return ResponseUtils.htmlResponse("Not Found!", HttpResponseStatus.NOT_FOUND);
                            }
                            String fileName = path.substring(index + 1);
                            file = FileUtils.listFiles(file, null, false).stream()
                                    .filter(f -> f.isFile() && (f.getName().equals(fileName) || f.getName().startsWith(fileName + ".")))
                                    .findFirst().orElseThrow(() -> new FileNotFoundException("Not Found!"));
                        } catch (Exception e) {
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
                        return ResponseUtils.htmlResponse("Error: " + e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return ResponseUtils.htmlResponse("Not Found!", HttpResponseStatus.NOT_FOUND);
                }
            }
        }
        if (useSecondProxyHostList.size() > 0) {
            ctx.getConnectionInfo().setUseSecondProxy(useSecondProxyHostList.contains(host));
        }
        if (notUseSecondProxyHostList.size() > 0) {
            ctx.getConnectionInfo().setUseSecondProxy(!notUseSecondProxyHostList.contains(host));
        }
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        return null;
    }

    public Set<String> getAllowUriList() {
        return allowUriList;
    }

    public Map<String, String> getRemoteUriMap() {
        return remoteUriMap;
    }

    public List<String> getUseSecondProxyHostList() {
        return useSecondProxyHostList;
    }

    public Set<String> getBlockUriList() {
        return blockUriList;
    }

    public List<String> getNotUseSecondProxyHostList() {
        return notUseSecondProxyHostList;
    }

    public boolean isBlockFlag() {
        return blockFlag;
    }

    public void setBlockFlag(boolean blockFlag) {
        this.blockFlag = blockFlag;
    }

    public void setAllowFlag(boolean allowFlag) {
        this.allowFlag = allowFlag;
    }

    public boolean isRemoteMapFlag() {
        return remoteMapFlag;
    }

    public void setRemoteMapFlag(boolean remoteMapFlag) {
        this.remoteMapFlag = remoteMapFlag;
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
