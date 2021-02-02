package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Dns;
import com.github.supermoonie.proxy.swing.entity.HostMap;
import com.github.supermoonie.proxy.swing.gui.panel.DnsDialog;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.j256.ormlite.dao.Dao;
import io.netty.util.internal.SocketUtils;

import javax.swing.*;
import java.awt.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2021/1/29
 */
public class DnsDialogController extends DnsDialog {

    private static final int DNS_DEFAULT_PORT = 53;

    public DnsDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getDnsTable().setEnabled(getEnableCheckBox().isSelected());
            getDnsAddButton().setEnabled(getEnableCheckBox().isSelected());
            getDnsRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
        getDnsAddButton().addActionListener(e -> {
            getDnsTable().clearSelection();
            getDnsTableModel().addRow(new Object[]{true, "", DNS_DEFAULT_PORT});
            getDnsTable().setShowHorizontalLines(true);
            getDnsTable().setShowVerticalLines(true);
        });
        getDnsRemoveButton().addActionListener(e -> {
            JTable dnsTable = getDnsTable();
            int[] selectedRows = dnsTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getDnsTableModel().removeRow(row);
            }
            dnsTable.clearSelection();
            dnsTable.setShowHorizontalLines(true);
            dnsTable.setShowVerticalLines(true);
        });
        getHostMapAddButton().addActionListener(e -> {
            getHostMapTable().clearSelection();
            getHostMapTableModel().addRow(new Object[]{true, "", ""});
            getHostMapTable().setShowHorizontalLines(true);
            getHostMapTable().setShowVerticalLines(true);
        });
        getHostMapRemoveButton().addActionListener(e -> {
            JTable hostMapTable = getHostMapTable();
            int[] selectedRows = hostMapTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getHostMapTableModel().removeRow(row);
            }
            hostMapTable.clearSelection();
            hostMapTable.setShowHorizontalLines(true);
            hostMapTable.setShowVerticalLines(true);
        });
        getOkButton().addActionListener(e -> {
            InternalProxy.DnsNameResolverConfig dnsConfig = ProxyManager.getInternalProxy().getDnsNameResolverConfig();
            boolean dnsEnable = getEnableCheckBox().isSelected();
            boolean sysHostEnable = getEnableSysDnsCheckBox().isSelected();
            dnsConfig.setUseSystemDefault(sysHostEnable);
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_DNS_ENABLE, dnsEnable);
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_DNS_LOCAL_HOST_ENABLE, sysHostEnable);
            Dao<Dns, Integer> dnsDao = DaoCollections.getDao(Dns.class);
            Dao<HostMap, Integer> hostMapDao = DaoCollections.getDao(HostMap.class);
            try {
                List<InetSocketAddress> dnsServerList = dnsConfig.getDnsServerList();
                dnsServerList.clear();
                dnsDao.deleteBuilder().delete();
                int rowCount = getDnsTable().getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    boolean enable = (boolean) getDnsTable().getValueAt(i, 0);
                    String ip = (String) getDnsTable().getValueAt(i, 1);
                    int port = (int) getDnsTable().getValueAt(i, 2);
                    if (enable && dnsEnable) {
                        dnsServerList.add(SocketUtils.socketAddress(ip, port));
                    }
                    Dns dns = new Dns();
                    dns.setEnable(enable ? Dns.ENABLE : Dns.DISABLE);
                    dns.setIp(ip);
                    dns.setPort(port);
                    dns.setTimeCreated(new Date());
                    dnsDao.create(dns);
                }
                hostMapDao.deleteBuilder().delete();
                Map<String, List<InetAddress>> dnsMap = InternalProxy.memoryDnsMap();
                rowCount = getHostMapTable().getRowCount();
                for (int i = 0; i < rowCount; i ++) {
                    boolean enable = (boolean) getHostMapTable().getValueAt(i, 0);
                    String host = (String) getHostMapTable().getValueAt(i, 1);
                    String ip = (String) getHostMapTable().getValueAt(i, 2);
                    if (enable) {
                        dnsMap.put(host, List.of(Inet4Address.getByName(ip)));
                    }
                    HostMap hm = new HostMap();
                    hm.setEnable(enable ? HostMap.ENABLE : HostMap.DISABLE);
                    hm.setHost(host);
                    hm.setIp(ip);
                    hm.setTimeCreated(new Date());
                    hostMapDao.create(hm);
                }
            } catch (SQLException | UnknownHostException t) {
                Application.showError(t);
            }
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
