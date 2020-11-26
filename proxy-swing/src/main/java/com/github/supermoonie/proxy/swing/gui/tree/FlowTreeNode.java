package com.github.supermoonie.proxy.swing.gui.tree;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;

import javax.swing.tree.DefaultMutableTreeNode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author supermoonie
 * @since 2020/11/26
 */
public class FlowTreeNode extends DefaultMutableTreeNode {

    public FlowTreeNode() {
    }

    public FlowTreeNode(Object userObject) {
        super(userObject);
    }

    public FlowTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public void add(ConnectionInfo connectionInfo, Request request, Response response) throws URISyntaxException {
        URI uri = new URI(connectionInfo.getUrl());
        String baseUrl = uri.getScheme() + "://" + uri.getAuthority();
        // Base node
        FlowTreeNode baseNode = findFirstChild(this, baseUrl).orElseGet(() -> {
            Flow baseFlow = new Flow();
            baseFlow.setUrl(baseUrl);
            baseFlow.setFlowType(FlowType.BASE_URL);
            FlowTreeNode node = new FlowTreeNode(baseFlow);
            this.add(node);
            return node;
        });
        if ("".equals(uri.getPath()) || "/".equals(uri.getPath())) {
            // Root path node
            Flow rootPathFlow = new Flow();
            rootPathFlow.setRequestId(request.getId());
            rootPathFlow.setUrl("/");
            rootPathFlow.setFlowType(FlowType.TARGET);
            FlowTreeNode rootPathNode = new FlowTreeNode(rootPathFlow);
            baseNode.add(rootPathNode);
        } else {

        }


    }

    public Optional<FlowTreeNode> findFirstChild(FlowTreeNode node, String url) {
        List<FlowTreeNode> children = findChildren(node, url);
        return Optional.ofNullable(children.size() == 0 ? null : children.get(0));
    }

    public List<FlowTreeNode> findChildren(FlowTreeNode node, String url) {
        List<FlowTreeNode> list = new LinkedList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            FlowTreeNode child = (FlowTreeNode) getChildAt(i);
            Flow flow = (Flow) child.getUserObject();
            if (flow.getUrl().equals(url)) {
                list.add(child);
            }
        }
        return list;
    }
}
