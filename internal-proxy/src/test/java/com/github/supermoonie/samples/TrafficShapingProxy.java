package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

import java.util.concurrent.TimeUnit;

/**
 * @author supermoonie
 * @since 2020/9/16
 */
public class TrafficShapingProxy {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy(null);
        proxy.setTrafficShaping(true);
        InternalProxy.TrafficShapingConfig trafficShapingConfig = proxy.getTrafficShapingConfig();
        trafficShapingConfig.setCheckInterval(1_000);
        trafficShapingConfig.setReadLimit(1024L);
        trafficShapingConfig.setWriteLimit(1024L);
        proxy.start();
        GlobalChannelTrafficShapingHandler trafficShapingHandler = proxy.getTrafficShapingHandler();
        TrafficCounter trafficCounter = trafficShapingHandler.trafficCounter();
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final long totalRead = trafficCounter.cumulativeReadBytes();
                final long totalWrite = trafficCounter.cumulativeWrittenBytes();
                System.out.println("total read: " + (totalRead >> 10) + " KB");
                System.out.println("total write: " + (totalWrite >> 10) + " KB");
                System.out.println(trafficCounter.toString());
            }
        }).start();
    }
}
