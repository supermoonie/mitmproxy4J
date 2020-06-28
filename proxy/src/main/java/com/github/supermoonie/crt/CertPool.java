package com.github.supermoonie.crt;

import com.github.supermoonie.server.HttpProxyServerConfig;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * CertPool
 *
 * @author wangc
 */
public class CertPool {

    private static final Map<Integer, Map<String, X509Certificate>> CERT_CACHE = new WeakHashMap<>();

    public static X509Certificate getCert(Integer port, String host, HttpProxyServerConfig serverConfig)
            throws Exception {
        X509Certificate cert = null;
        if (host != null) {
            Map<String, X509Certificate> portCertCache = CERT_CACHE.computeIfAbsent(port, k -> new HashMap<>(5));
            String key = host.trim().toLowerCase();
            if (portCertCache.containsKey(key)) {
                return portCertCache.get(key);
            } else {
                cert = CertUtil.genCert(serverConfig.getIssuer(), serverConfig.getCaPriKey(),
                        serverConfig.getCaNotBefore(), serverConfig.getCaNotAfter(),
                        serverConfig.getServerPubKey(), key);
                portCertCache.put(key, cert);
            }
        }
        return cert;
    }

    public static void clear() {
        CERT_CACHE.clear();
    }
}
