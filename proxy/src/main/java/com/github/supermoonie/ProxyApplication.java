package com.github.supermoonie;

import com.github.supermoonie.intercept.HttpProxyInterceptInitializer;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import com.github.supermoonie.intercept.common.BaseFullReqIntercept;
import com.github.supermoonie.intercept.common.BaseFullResIntercept;
import com.github.supermoonie.server.HttpProxyServer;
import com.github.supermoonie.server.HttpProxyServerConfig;
import com.github.supermoonie.util.HttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

/**
 * Hello world!
 *
 * @author wangc
 */
public class ProxyApplication
{
    public static void main(String[] args) {
        System.out.println("start proxy server");
        int port = 10801;
        HttpProxyServerConfig serverConfig = new HttpProxyServerConfig();
        serverConfig.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(serverConfig)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new BaseFullReqIntercept() {
                            @Override
                            public boolean match(HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
                                return true;
                            }

                            @Override
                            public void handelRequest(FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {

//                                super.handelRequest(httpRequest, pipeline);
                            }
                        });
                        pipeline.addLast(new BaseFullResIntercept() {

                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {

                                //在匹配到百度首页时插入js
                                System.out.println(httpRequest.uri());
                                return HttpUtil.isHtml(httpRequest, httpResponse);
//                                return HttpUtil.checkUrl(pipeline.getHttpRequest(), "^http://httpbin.org/$")
//                                        && HttpUtil.isHtml(httpRequest, httpResponse);
                            }

                            @Override
                            public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                //打印原始响应信息
//                                System.out.println(httpResponse.toString());
//                                System.out.println(httpResponse.content().toString(Charset.defaultCharset()));
                                //修改响应头和响应体

                                httpResponse.headers().set("handel", "edit head");
                    /*int index = ByteUtil.findText(httpResponse.content(), "<head>");
                    ByteUtil.insertText(httpResponse.content(), index, "<script>alert(1)</script>");*/
                                httpResponse.content().writeBytes("<script>alert('hello proxyee')</script>".getBytes());
                            }
                        });
                    }
                })
                .start(port);
    }
}
