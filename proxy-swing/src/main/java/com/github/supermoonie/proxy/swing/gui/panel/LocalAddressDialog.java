package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.util.ClipboardUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author supermoonie
 * @date 2021-02-02
 */
public class LocalAddressDialog extends JDialog {

    private final DefaultTableModel addressTableModel = new DefaultTableModel(null, new String[]{"Display Name", "IP Address"});
    private final JTable addressTable = new JTable(addressTableModel) {
        private final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class};

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public LocalAddressDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        // container
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
        BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);

        // address table
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addressPanel.add(new JScrollPane(addressTable));
        container.add(addressPanel);
        List<NetworkInterfaceInfo> networkInterfaceInfoList = getNetworkInterfaceInfo();
        for (NetworkInterfaceInfo info : networkInterfaceInfoList) {
            addressTableModel.addRow(new Object[]{info.getDisplayName(), info.getIp()});
        }
//        addressTable.getColumnModel().getColumn(0).setPreferredWidth(400);
//        addressTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        addressTable.setCellSelectionEnabled(true);
        addressTable.setShowHorizontalLines(true);
        addressTable.setShowVerticalLines(true);
        addressTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean flag = (e.isControlDown() || e.isMetaDown()) && (e.getKeyCode() == KeyEvent.VK_C);
                if (flag) {
                    ClipboardUtil.copySelectedCell(addressTable);
                }
            }
        });

        super.setResizable(false);
        super.getContentPane().add(container);
        super.pack();
        super.setLocationRelativeTo(owner);
    }

    public List<NetworkInterfaceInfo> getNetworkInterfaceInfo() {
        List<NetworkInterfaceInfo> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface
                    .getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                String ip = null;
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress.getHostAddress().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                        ip = inetAddress.getHostAddress();
                    }
                }
                if (null != ip) {
                    String displayName = netint.getDisplayName();
                    String name = netint.getName();
                    result.add(new NetworkInterfaceInfo(displayName, name, ip));
                }
            }
            return result;
        } catch (SocketException e) {
            return result;
        }
    }

    public DefaultTableModel getAddressTableModel() {
        return addressTableModel;
    }

    public JTable getAddressTable() {
        return addressTable;
    }

    private static class NetworkInterfaceInfo {
        private String displayName;
        private String name;
        private String ip;

        public NetworkInterfaceInfo() {
        }

        public NetworkInterfaceInfo(String displayName, String name, String ip) {
            this.displayName = displayName;
            this.name = name;
            this.ip = ip;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        @Override
        public String toString() {
            return "NetworkInterfaceInfo{" +
                    "displayName='" + displayName + '\'' +
                    ", name='" + name + '\'' +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }
}
