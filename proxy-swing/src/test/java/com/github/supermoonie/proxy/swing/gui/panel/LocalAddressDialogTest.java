package com.github.supermoonie.proxy.swing.gui.panel;

import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @date 2021-02-02
 */
public class LocalAddressDialogTest {

    @Test
    public void t() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface
                .getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            displayInterfaceInformation(netint);
        }

    }

    static void displayInterfaceInformation(NetworkInterface netint)
            throws SocketException {
        System.out.printf("Display name: %s\n", netint.getDisplayName());
        System.out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            System.out.printf("InetAddress: %s\n", inetAddress.getHostAddress());
        }
        System.out.printf("\n");
    }

}