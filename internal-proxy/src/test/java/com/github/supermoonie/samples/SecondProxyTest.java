package com.github.supermoonie.samples;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.supermoonie.auto.AutoChrome;
import com.github.supermoonie.proxy.*;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
public class SecondProxyTest {

    private static final int DNS_DEFAULT_PORT = 53;

    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(
            5,
            new BasicThreadFactory.Builder()
                    .namingPattern("schedule-%d")
                    .daemon(false)
                    .uncaughtExceptionHandler((thread, throwable) -> {
                        String error = String.format("thread: %s, error: %s", thread.toString(), throwable.getMessage());
                        System.out.println(error);
                    }).build(), (r, executor) -> System.out.println("Thread: " + r.toString() + ", rejected by: " + executor.toString()));
    ;

    public static void main(String[] args) throws Exception {

        final SecondProxyConfig secondProxyConfig = new SecondProxyConfig();
        secondProxyConfig.setHost("127.0.0.1");
        secondProxyConfig.setPort(7890);
        secondProxyConfig.setProxyType(ProxyType.HTTP);
//        SwingUtilities.invokeLater(() -> {
//            JOptionPane.showMessageDialog(null, ".....");
//            try {
//                Queue<AutoChrome> chromeList = new LinkedBlockingDeque<>() {{
//                    add(new AutoChrome(9111, "about:blank", 5_000));
//                    add(new AutoChrome(9222, "about:blank", 5_000));
//                    add(new AutoChrome(9333, "about:blank", 5_000));
//                    add(new AutoChrome(9444, "about:blank", 5_000));
//                    add(new AutoChrome(9555, "about:blank", 5_000));
//                    add(new AutoChrome(9666, "about:blank", 5_000));
//                    add(new AutoChrome(9777, "about:blank", 5_000));
//                    add(new AutoChrome(9888, "about:blank", 5_000));
//                    add(new AutoChrome(9999, "about:blank", 5_000));
//                }};
////                scheduledExecutor.scheduleAtFixedRate(() -> {
////                    try {
////                        HttpClient httpClient = HttpClient.newBuilder()
////                                .version(HttpClient.Version.HTTP_1_1)
////                                .followRedirects(HttpClient.Redirect.ALWAYS)
////                                .build();
////                        String proxyUrl = "http://list.sky-ip.net/user_get_ip_list?token=SbnzU8qJ7CLyoIVs1623746522101&qty=1&country=&time=1&format=txt&protocol=http";
////                        HttpResponse<String> proxyRes = httpClient.send(java.net.http.HttpRequest.newBuilder().uri(URI.create(proxyUrl)).build(), HttpResponse.BodyHandlers.ofString());
////                        String proxy = proxyRes.body();
////                        synchronized (SecondProxyTest.class) {
////                            String[] split = proxy.split(":");
////                            secondProxyConfig.setHost(split[0]);
////                            secondProxyConfig.setPort(Integer.parseInt(split[1]));
////                            for (AutoChrome autoChrome : chromeList) {
////                                autoChrome.clearBrowserCache();
////                                autoChrome.clearBrowserCookies();
////                                autoChrome.getStorage().clearDataForOrigin("www.hermes.com", StorageType.all.value);
////                            }
////                        }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }, 1, 45, TimeUnit.SECONDS);
//                scheduledExecutor.scheduleAtFixedRate(() -> {
//                    AutoChrome autoChrome = chromeList.poll();
//                    if (null == autoChrome) {
//                        return;
//                    }
//                    try {
//                        HttpClient httpClient = HttpClient.newBuilder()
//                                .version(HttpClient.Version.HTTP_1_1)
//                                .followRedirects(HttpClient.Redirect.ALWAYS)
//                                .build();
//                        String proxyUrl = "http://list.sky-ip.net/user_get_ip_list?token=SbnzU8qJ7CLyoIVs1623746522101&qty=1&country=&time=6&format=txt&protocol=http";
//                        HttpResponse<String> proxyRes = httpClient.send(java.net.http.HttpRequest.newBuilder().uri(URI.create(proxyUrl)).build(), HttpResponse.BodyHandlers.ofString());
//                        String proxy = proxyRes.body();
//                        String[] split = proxy.split(":");
//                        secondProxyConfig.setHost(split[0]);
//                        secondProxyConfig.setPort(Integer.parseInt(split[1]));
////                        autoChrome.clearBrowserCache();
////                        autoChrome.clearBrowserCookies();
////                        autoChrome.getStorage().clearDataForOrigin("www.hermes.com", StorageType.all.value);
//                        autoChrome.navigateUntilDomReady("https://www.hermes.com/fr/fr/", 20_000);
//                        autoChrome.navigate("https://bck.hermes.com/products?locale=nl_en&category=WOMENBAGSBAGSCLUTCHES&sort=relevance&offset=36&pagesize=36");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        chromeList.offer(autoChrome);
//                    }
//                }, 10, 10, TimeUnit.SECONDS);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });

        RequestIntercept requestIntercept = (ctx, request) -> {
            ConnectionInfo connectionInfo = ctx.getConnectionInfo();
            if (connectionInfo.getUrl().contains("js.datadome.co/tags.js")) {
                try {
                    byte[] bytes = FileUtils.readFileToByteArray(new File("D:\\java\\warMap\\tags.js"));
                    ByteBuf buf = Unpooled.wrappedBuffer(bytes);
                    FullHttpResponse response =
                            new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, buf);
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/javascript");
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        };

        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            requestIntercepts.put("req-intercept", requestIntercept);
//            requestIntercepts.put("--", new ConfigurableIntercept() {
//                @Override
//                public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
//                    ctx.getConnectionInfo().setUseSecondProxy(true);
//                    ctx.getConnectionInfo().setSecondProxyConfig(secondProxyConfig);
//                    return null;
//                }
//            });
        });
//        InternalProxy.DnsNameResolverConfig dnsNameResolverConfig = proxy.getDnsNameResolverConfig();
//        dnsNameResolverConfig.setUseSystemDefault(true);
//        List<InetSocketAddress> dnsServerList = dnsNameResolverConfig.getDnsServerList();
//        dnsServerList.add(SocketUtils.socketAddress("8.8.8.8", DNS_DEFAULT_PORT));
//        dnsServerList.add(SocketUtils.socketAddress("8.8.4.4", DNS_DEFAULT_PORT));
//        dnsServerList.add(SocketUtils.socketAddress("114.114.114.114", DNS_DEFAULT_PORT));

        proxy.setWorkerThreads(new NioEventLoopGroup(16));
        proxy.setBossThreads(new NioEventLoopGroup(1));
        proxy.setProxyThreads(new NioEventLoopGroup(16));
        proxy.setPort(10801);
        proxy.start();
    }

    @Test
    public void testDatadome() throws Exception {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        String proxyUrl = "https://api-js.datadome.co/js/";
        Map<Object, Object> formData = new HashMap<>();
        formData.put("jsData", "{\"ttst\":21.100001096725464,\"ifov\":false,\"wdifts\":false,\"wdifrm\":false,\"wdif\":false,\"br_h\":952,\"br_w\":1904,\"br_oh\":1040,\"br_ow\":1920,\"nddc\":1,\"rs_h\":1080,\"rs_w\":1920,\"rs_cd\":24,\"phe\":false,\"nm\":false,\"jsf\":false,\"ua\":\"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36\",\"lg\":\"zh-CN\",\"pr\":1,\"hc\":8,\"ars_h\":1040,\"ars_w\":1920,\"tz\":-480,\"str_ss\":true,\"str_ls\":true,\"str_idb\":true,\"str_odb\":true,\"plgod\":false,\"plg\":3,\"plgne\":true,\"plgre\":true,\"plgof\":false,\"plggt\":false,\"pltod\":false,\"hcovdr\":false,\"plovdr\":false,\"ftsovdr\":false,\"lb\":false,\"eva\":33,\"lo\":true,\"ts_mtp\":1,\"ts_tec\":true,\"ts_tsa\":true,\"vnd\":\"Google Inc.\",\"bid\":\"NA\",\"mmt\":\"application/pdf,application/x-google-chrome-pdf,application/x-nacl,application/x-pnacl\",\"plu\":\"Chrome PDF Plugin,Chrome PDF Viewer,Native Client\",\"hdn\":false,\"awe\":false,\"geb\":false,\"dat\":false,\"med\":\"defined\",\"aco\":\"probably\",\"acots\":false,\"acmp\":\"probably\",\"acmpts\":true,\"acw\":\"probably\",\"acwts\":false,\"acma\":\"maybe\",\"acmats\":false,\"acaa\":\"probably\",\"acaats\":true,\"ac3\":\"\",\"ac3ts\":false,\"acf\":\"probably\",\"acfts\":false,\"acmp4\":\"maybe\",\"acmp4ts\":false,\"acmp3\":\"probably\",\"acmp3ts\":false,\"acwm\":\"maybe\",\"acwmts\":false,\"ocpt\":false,\"vco\":\"probably\",\"vcots\":false,\"vch\":\"probably\",\"vchts\":true,\"vcw\":\"probably\",\"vcwts\":true,\"vc3\":\"maybe\",\"vc3ts\":false,\"vcmp\":\"\",\"vcmpts\":false,\"vcq\":\"\",\"vcqts\":false,\"vc1\":\"probably\",\"vc1ts\":false,\"dvm\":32,\"sqt\":false,\"so\":\"landscape-primary\",\"wbd\":false,\"wbdm\":false,\"wdw\":true,\"cokys\":\"bG9hZFRpbWVzY3NpYXBwcnVudGltZQ==L=\",\"ecpc\":false,\"lgs\":true,\"lgsod\":false,\"bcda\":false,\"idn\":true,\"capi\":false,\"svde\":false,\"vpbq\":true,\"xr\":true,\"bgav\":true,\"rri\":true,\"idfr\":true,\"ancs\":true,\"inlc\":true,\"cgca\":true,\"inlf\":true,\"tecd\":true,\"sbct\":true,\"aflt\":true,\"rgp\":true,\"bint\":true,\"spwn\":false,\"emt\":false,\"bfr\":false,\"dbov\":false,\"glvd\":\"Microsoft\",\"glrd\":\"ANGLE (Intel(R) HD Graphics 5300 Direct3D11 vs_5_0 ps_5_0)\",\"tagpu\":7.799999952316284,\"prm\":true,\"tzp\":\"Asia/Shanghai\",\"cvs\":true,\"usb\":\"defined\",\"dcok\":\".hermes.com\",\"ewsi\":false,\"tbce\":0}");
        formData.put("events", "[{\"source\":{\"x\":1609,\"y\":282},\"message\":\"touch start\",\"date\":1630124935782,\"id\":3},{\"source\":{\"x\":1609,\"y\":282},\"message\":\"touch end\",\"date\":1630124935867,\"id\":4},{\"source\":{\"x\":1609,\"y\":292},\"message\":\"mouse move\",\"date\":1630124935869,\"id\":0},{\"source\":{\"x\":1609,\"y\":292},\"message\":\"mouse click\",\"date\":1630124935900,\"id\":1}]");
        formData.put("eventCounters", "{\"mouse move\":1,\"mouse click\":1,\"scroll\":0,\"touch start\":1,\"touch end\":1,\"touch move\":0,\"key press\":0,\"key down\":0,\"key up\":0}");
        formData.put("jsType", "le");
        formData.put("cid", "G14FIw7LenIWWbJ5uWAEPBjrzQcbGr9cxaZhR5em3narbMMWuaD8UsMJM2X5kFGcYM8JxWxmC2dDI.K9~jTVb-peNeBHH~5WMJJsvOjs5a");
        formData.put("ddk", "2211F522B61E269B869FA6EAFFB5E1");
        formData.put("Referer", "https://www.hermes.com/fr/fr/");
        formData.put("request", "/fr/fr/");
        formData.put("responsePage", "origin");
        formData.put("ddv", "4.1.60");
        HttpResponse<String> proxyRes = httpClient.send(java.net.http.HttpRequest.newBuilder().uri(URI.create(proxyUrl))
                .POST(ofFormData(formData))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "zh-CN,zh;q=0.9")
                .header("dnt", "1")
                .header("referer", "https://www.hermes.com/")
                .header("sec-ch-ua", "\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "cross-site")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                .build(), HttpResponse.BodyHandlers.ofString());
        String datadomeBody = proxyRes.body();
        System.out.println(datadomeBody);
        JSONObject datadomeJson = JSON.parseObject(datadomeBody);
        String datadomeCookie = datadomeJson.getString("cookie");
        String[] datadomeCookieArr = datadomeCookie.split(";");
        String datadome = Arrays.stream(datadomeCookieArr).filter(item -> item.startsWith("datadome=")).findFirst().orElse(null);
        datadome = datadome.replaceAll("datadome=", "");
        System.out.println(datadome);

        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create("https://bck.hermes.com/add-to-cart"))
                .timeout(Duration.ofMinutes(1))
                .header("content-type", "application/json")
                .header("accept", "application/json, text/plain, */*")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "zh-CN,zh;q=0.9")
                .header("dnt", "1")
                .header("cookie", String.format("correlation_id=7af746a6aa80d5bf6fcfee27d179756e; _cs_mk=0.5015408925260674_1630123335043; _gcl_au=1.1.952097457.1630123335; _gid=GA1.2.2054457823.1630123335; _cs_c=1; _fbp=fb.1.1630123335718.1985523086; _gat_UA-64545050-1=1; ECOM_SESS=1qd31atqcgek9362v60pa8fmf3; datadome=%s; _cs_id=cf71bd5b-53f8-a75f-c768-429072597201.1630123335.1.1630124932.1630123335.1.1664287335553; _cs_s=4.0.0.1630126732613; _ga=GA1.2.1745640506.1630123335; _ga_Y862HCHCQ7=GS1.1.1630123335.1.1.1630124933.0", ""))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"locale\":\"fr_fr\",\"items\":[{\"category\":\"direct\",\"sku\":\"W054745WW00\"}]}"));
        builder.setHeader("sec-fetch-site", "same-site");
        builder.setHeader("sec-fetch-mode", "cors");
        builder.setHeader("sec-fetch-dest", "empty");
        builder.setHeader("referer", "https://www.hermes.com/");
        builder.setHeader("accept-encoding", "gzip, deflate, br");
        builder.setHeader("accept-language", "en-US,en;q=0.9");
        HttpResponse<byte[]> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        byte[] body = resp.body();
        System.out.println(new String(body));
        String s = GZIPUtils.uncompressToString(body, "UTF-8");
        System.out.println(s);
    }

    public static java.net.http.HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return java.net.http.HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
