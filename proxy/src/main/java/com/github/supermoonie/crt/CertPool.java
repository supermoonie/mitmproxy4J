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

    private static Map<Integer, Map<String, X509Certificate>> certCache = new WeakHashMap<>();

    public static X509Certificate getCert(Integer port, String host, HttpProxyServerConfig serverConfig)
            throws Exception {
        X509Certificate cert = null;
        if (host != null) {
            Map<String, X509Certificate> portCertCache = certCache.computeIfAbsent(port, k -> new HashMap<>());
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
        certCache.clear();
    }
}
