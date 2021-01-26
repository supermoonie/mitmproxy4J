package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.SecondProxyConfig;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.ExternalProxy;
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

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
//        boolean proxyEnable = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_EXTERNAL_PROXY_ENABLE, ApplicationPreferences.DEFAULT_EXTERNAL_PROXY_ENABLE);
        boolean proxyEnable = true;
        if (proxyEnable) {
            Dao<ExternalProxy, Integer> externalProxyDao = DaoCollections.getDao(ExternalProxy.class);
            try {
                List<ExternalProxy> externalProxyList = externalProxyDao.queryForAll();
                if (null == externalProxyList || 0 == externalProxyList.size()) {
                    return null;
                }
                String remoteHost = ctx.getConnectionInfo().getRemoteHost();
                Optional<ExternalProxy> proxy = Optional.empty();
                if (null != remoteHost) {
                    proxy = externalProxyList.stream().filter(item ->
                            (item.getHost().equals("*") || remoteHost.matches(item.getHost()))
                                    && item.getEnable().equals(ExternalProxy.ENABLE)
                    ).findFirst();
                } else {
                    List<InetAddress> remoteAddressList = ctx.getConnectionInfo().getRemoteAddressList();
                    if (null != remoteAddressList && remoteAddressList.size() > 0) {
                        proxy = externalProxyList.stream().filter(item ->
                                (item.getHost().equals("*") || remoteAddressList.stream().anyMatch(remote -> remote.getHostAddress().matches(item.getHost())))
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
