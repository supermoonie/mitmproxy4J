package com.github.supermoonie.proxy.dns;

import com.github.supermoonie.proxy.ConnectionInfo;
import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
     * @param executor       the {@link EventExecutor} which is used to notify the listeners of the {@link Future} returned
     *                       by {@link #resolve(String)}
     * @param connectionInfo {@link ConnectionInfo}
     * @param resolvers      the {@link NameResolver}s to be tried sequentially
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
        doResolveRec(inetHost, promise, 0);
    }

    private void doResolveRec(final String inetHost,
                              final Promise<InetAddress> promise,
                              final int resolverIndex) {
        if (resolverIndex >= resolvers.length) {
            promise.setFailure(new UnknownHostException(inetHost));
        } else {
            NameResolver<InetAddress> resolver = resolvers[resolverIndex];
            resolver.resolveAll(inetHost).addListener((FutureListener<List<InetAddress>>) future -> {
                if (future.isSuccess()) {
                    connectionInfo.setDnsEndTime(System.currentTimeMillis());
                    List<InetAddress> inetAddresses = future.getNow();
                    int numAddresses = inetAddresses.size();
                    if (numAddresses > 0) {
                        log.debug("host: {}, answer: {}", inetHost, inetAddresses.toString());
                        connectionInfo.setRemoteAddressList(inetAddresses);
                        promise.setSuccess(inetAddresses.get(randomIndex(numAddresses)));
                    } else {
                        doResolveRec(inetHost, promise, resolverIndex + 1);
                    }
                } else {
                    doResolveRec(inetHost, promise, resolverIndex + 1);
                }
            });
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        connectionInfo.setDnsStartTime(System.currentTimeMillis());
        doResolveAllRec(inetHost, promise, 0);
    }

    private void doResolveAllRec(final String inetHost,
                                 final Promise<List<InetAddress>> promise,
                                 final int resolverIndex) {
        if (resolverIndex >= resolvers.length) {
            promise.setFailure(new UnknownHostException(inetHost));
        } else {
            NameResolver<InetAddress> resolver = resolvers[resolverIndex];
            resolver.resolveAll(inetHost).addListener((FutureListener<List<InetAddress>>) future -> {
                if (future.isSuccess()) {
                    List<InetAddress> inetAddresses = future.getNow();
                    if (!inetAddresses.isEmpty()) {
                        connectionInfo.setDnsEndTime(System.currentTimeMillis());
                        connectionInfo.setRemoteAddressList(inetAddresses);
                        log.debug("host: {}, answer: {}", inetHost, inetAddresses.toString());
                        List<InetAddress> result = new ArrayList<InetAddress>(inetAddresses);
                        Collections.rotate(result, randomIndex(inetAddresses.size()));
                        promise.setSuccess(result);
                    } else {
                        doResolveAllRec(inetHost, promise, resolverIndex + 1);
                    }
                } else {
                    doResolveAllRec(inetHost, promise, resolverIndex + 1);
                }
            });
        }
    }

    private static int randomIndex(int numAddresses) {
        return numAddresses == 1 ? 0 : PlatformDependent.threadLocalRandom().nextInt(numAddresses);
    }
}
