package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.SecondProxyConfig;
import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.entity.ExternalProxy;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.j256.ormlite.dao.Dao;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author supermoonie
 * @since 2021/1/24
 */
public class ExternalProxyIntercept implements RequestIntercept {

    private final Logger log = LoggerFactory.getLogger(ExternalProxyIntercept.class);

    public static final ExternalProxyIntercept INSTANCE = new ExternalProxyIntercept();
    private static final String LOCAL_HOST = "localhost";
    private static final String LOCAL_IP = "127.0.0.1";

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        boolean proxyEnable = AppPreferences.getState().getBoolean(AppPreferences.KEY_EXTERNAL_PROXY_ENABLE, AppPreferences.DEFAULT_EXTERNAL_PROXY_ENABLE);
        if (proxyEnable) {
            Dao<ExternalProxy, Integer> externalProxyDao = DaoCollections.getDao(ExternalProxy.class);
            try {
                List<ExternalProxy> externalProxyList = externalProxyDao.queryForAll();
                if (null == externalProxyList || 0 == externalProxyList.size()) {
                    return null;
                }
                boolean bypassLocalhost = AppPreferences.getState().getBoolean(AppPreferences.KEY_EXTERNAL_PROXY_BYPASS_LOCALHOST, AppPreferences.DEFAULT_EXTERNAL_PROXY_BYPASS_LOCALHOST);
                StringBuilder bypassHostListBuilder = new StringBuilder(AppPreferences.getState().get(AppPreferences.KEY_EXTERNAL_PROXY_BYPASS_LIST, ""));
                if (bypassLocalhost) {
                    bypassHostListBuilder.append(",").append(LOCAL_HOST).append(",").append(LOCAL_IP);
                }
                String bypassHostList = bypassHostListBuilder.toString();
                String remoteHost = ctx.getConnectionInfo().getRemoteHost();
                Optional<ExternalProxy> proxy = Optional.empty();
                if (null != remoteHost) {
                    if (bypassHostList.contains(remoteHost)) {
                        return null;
                    }
                    proxy = externalProxyList.stream().filter(item ->
                            ("*".equals(item.getHost()) || remoteHost.matches(item.getHost()))
                                    && item.getEnable().equals(ExternalProxy.ENABLE)
                    ).findFirst();
                } else {
                    List<InetAddress> remoteAddressList = ctx.getConnectionInfo().getRemoteAddressList();
                    if (null != remoteAddressList && remoteAddressList.size() > 0) {
                        boolean bypassFlag = remoteAddressList.stream().anyMatch(item -> bypassHostList.contains(item.getHostAddress()));
                        if (bypassFlag) {
                            return null;
                        }
                        proxy = externalProxyList.stream().filter(item ->
                                ("*".equals(item.getHost()) || remoteAddressList.stream().anyMatch(remote -> remote.getHostAddress().matches(item.getHost())))
                                        && item.getEnable().equals(ExternalProxy.ENABLE)
                        ).findFirst();
                    }
                }
                proxy.ifPresent(p -> {
                    ctx.getConnectionInfo().setUseSecondProxy(true);
                    SecondProxyConfig secondProxyConfig = new SecondProxyConfig();
                    secondProxyConfig.setHost(p.getProxyHost());
                    secondProxyConfig.setPort(p.getProxyPort());
                    secondProxyConfig.setProxyType(ProxyType.valueOf(p.getProxyType()).orElseThrow());
                    secondProxyConfig.setUsername(p.getProxyUser());
                    secondProxyConfig.setPassword(p.getProxyPwd());
                    ctx.getConnectionInfo().setSecondProxyConfig(secondProxyConfig);
                });
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
        return null;
    }
}
