package com.github.supermoonie.proxy.swing.gui;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author supermoonie
 * @since 2020/12/1
 */
public class ProxyFrameHelper {

    public static final AtomicInteger SHOWING_REQUEST_ID = new AtomicInteger(-1);
    public static final AtomicReference<String> SHOWING_REQUEST_TAB_NAME = new AtomicReference<>();
    public static final AtomicReference<String> SHOWING_RESPONSE_TAB_NAME = new AtomicReference<>();

    private ProxyFrameHelper() {
        throw new UnsupportedOperationException();
    }

    public static Flow getSelectedFlow() {
        ProxyFrame proxyFrame = Application.PROXY_FRAME;
        JPanel selectedComponent = (JPanel) proxyFrame.getFlowTabPane().getSelectedComponent();
        Flow flow;
        if (selectedComponent.equals(proxyFrame.getStructureTab())) {
            JTree flowTree = proxyFrame.getFlowTree();
            FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
            if (null == node || !node.isLeaf()) {
                return null;
            }
            flow = (Flow) node.getUserObject();
        } else {
            FlowList flowList = proxyFrame.getFlowList();
            flow = flowList.getSelectedValue();
        }
        return flow;
    }

    public static void showResponseCodeArea() {

    }
}
