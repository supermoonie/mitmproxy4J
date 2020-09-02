package com.github.supermoonie;

import com.github.supermoonie.exception.HttpProxyExceptionHandle;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import com.github.supermoonie.intercept.common.BaseFullReqIntercept;
import com.github.supermoonie.intercept.common.BaseFullResIntercept;
import com.github.supermoonie.server.HttpProxyServer;
import com.github.supermoonie.server.HttpProxyServerConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Hello world!
 *
 * @author wangc
 */
public class ProxyApplication
{
    public static void main(String[] args) {
        int port = 10800;
        HttpProxyServerConfig serverConfig = new HttpProxyServerConfig();
        serverConfig.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(serverConfig)
                .httpProxyExceptionHandle(new HttpProxyExceptionHandle() {

                    @Override
                    public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
                        cause.printStackTrace();
                    }

                    @Override
                    public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause) throws Exception {
                        cause.printStackTrace();
                    }
                })
                .proxyInterceptInitializer(pipeline -> {
                    pipeline.addLast(new BaseFullReqIntercept() {
                        @Override
                        public boolean match(HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
                            return true;
                        }

                        @Override
                        public void handelRequest(FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
                            System.out.println(httpRequest.uri());
                        }
                    });
                    pipeline.addLast(new BaseFullResIntercept() {

                        @Override
                        public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                            return true;
                        }

                        @Override
                        public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {

                        }
                    });
                })
                .start(port);
    }
}
