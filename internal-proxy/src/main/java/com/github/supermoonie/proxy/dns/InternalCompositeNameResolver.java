package com.github.supermoonie.proxy.dns;

import com.github.supermoonie.proxy.ConnectionInfo;
import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * @author supermoonie
 * @date 2020-11-14
 */
public class InternalCompositeNameResolver extends SimpleNameResolver<InetAddress> {

    private final Logger log = LoggerFactory.getLogger(InternalCompositeNameResolver.class);

    private final NameResolver<InetAddress>[] resolvers;

    private final ConnectionInfo connectionInfo;

    /**
     * @param executor the {@link EventExecutor} which is used to notify the listeners of the {@link Future} returned
     *                 by {@link #resolve(String)}
     * @param connectionInfo {@link ConnectionInfo}
     * @param resolvers the {@link NameResolver}s to be tried sequentially
     */
    @SafeVarargs
    public InternalCompositeNameResolver(EventExecutor executor, ConnectionInfo connectionInfo, NameResolver<InetAddress>... resolvers) {
        super(executor);
        this.connectionInfo = connectionInfo;
        checkNotNull(resolvers, "resolvers");
        for (int i = 0; i < resolvers.length; i++) {
            ObjectUtil.checkNotNull(resolvers[i], "resolvers[" + i + ']');
        }
        if (resolvers.length < 2) {
            throw new IllegalArgumentException("resolvers: " + Arrays.asList(resolvers) +
                    " (expected: at least 2 resolvers)");
        }
        this.resolvers = resolvers.clone();
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        connectionInfo.setDnsStartTime(System.currentTimeMillis());
        doResolveRec(inetHost, promise, 0, null);
    }

    private void doResolveRec(final String inetHost,
                              final Promise<InetAddress> promise,
                              final int resolverIndex,
                              Throwable lastFailure) throws Exception {
        if (resolverIndex >= resolvers.length) {
            promise.setFailure(lastFailure);
        } else {
            NameResolver<InetAddress> resolver = resolvers[resolverIndex];
            resolver.resolve(inetHost).addListener(new FutureListener<InetAddress>() {
                @Override
                public void operationComplete(Future<InetAddress> future) throws Exception {
                    if (future.isSuccess()) {
                        connectionInfo.setDnsEndTime(System.currentTimeMillis());
                        InetAddress inetAddress = future.getNow();
                        String dnsServer = dnsServerName(resolver);
                        log.debug("host: {}, dns: {}, answer: {}", inetHost, dnsServer, inetAddress.toString());
                        connectionInfo.setRemoteAddressList(List.of(inetAddress));
                        connectionInfo.setDnsServer(dnsServer);
                        promise.setSuccess(inetAddress);
                    } else {
                        doResolveRec(inetHost, promise, resolverIndex + 1, future.cause());
                    }
                }
            });
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        connectionInfo.setDnsStartTime(System.currentTimeMillis());
        doResolveAllRec(inetHost, promise, 0, null);
    }

    private void doResolveAllRec(final String inetHost,
                                 final Promise<List<InetAddress>> promise,
                                 final int resolverIndex,
                                 Throwable lastFailure) throws Exception {
        if (resolverIndex >= resolvers.length) {
            promise.setFailure(lastFailure);
        } else {
            NameResolver<InetAddress> resolver = resolvers[resolverIndex];
            resolver.resolveAll(inetHost).addListener(new FutureListener<List<InetAddress>>() {
                @Override
                public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                    if (future.isSuccess()) {
                        connectionInfo.setDnsEndTime(System.currentTimeMillis());
                        List<InetAddress> inetAddresses = future.get();
                        String dnsServer = dnsServerName(resolver);
                        log.debug("host: {}, dns: {}, answer: {}", inetHost, dnsServer, inetAddresses.toString());
                        connectionInfo.setRemoteAddressList(inetAddresses);
                        connectionInfo.setDnsServer(dnsServer);
                        promise.setSuccess(inetAddresses);
                    } else {
                        doResolveAllRec(inetHost, promise, resolverIndex + 1, future.cause());
                    }
                }
            });
        }
    }

    private String dnsServerName(NameResolver<InetAddress> resolver) {
        if (resolver instanceof ConfigurableNameResolver) {
            return "MemoryDnsMap";
        } else if (resolver instanceof InternalSingletonDnsServerAddressStreamProvider) {
            InternalSingletonDnsServerAddressStreamProvider provider = (InternalSingletonDnsServerAddressStreamProvider) resolver;
            return String.format("SingletonDnsServer:%s", provider.getAddress().getHostString());
        } else {
            List<InetSocketAddress> addresses = DefaultDnsServerAddressStreamProvider.defaultAddressList();
            String addr = addresses.stream().map(InetSocketAddress::getHostString).collect(Collectors.toList()).toString();
            return String.format("SystemDnsProvider:%s", addr);
        }
    }
}
