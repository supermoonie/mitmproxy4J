package com.github.supermoonie.proxy.swing.gui.flow;

import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;

import javax.swing.*;
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

    public void update(Request request, Response response) {
        FlowTreeNode leaf = findLeaf(this, request.getId());
        if (null == leaf) {
            return;
        }
        Flow flow = (Flow) leaf.getUserObject();
        if (null == response) {
            flow.setIcon(SvgIcons.DOWNLOAD);
        } else {
            flow.setContentType(response.getContentType());
            flow.setResponseId(response.getId());
            flow.setStatus(response.getStatus());
            flow.setIcon(SvgIcons.loadIcon(response.getStatus(), response.getContentType()));
        }
    }

    public void add(String url, int requestId, Icon icon) throws URISyntaxException {
        URI uri = new URI(url);
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
            // Target node
            Flow rootPathFlow = new Flow();
            rootPathFlow.setRequestId(requestId);
            rootPathFlow.setUrl("/");
            rootPathFlow.setFlowType(FlowType.TARGET);
            rootPathFlow.setIcon(icon);
            FlowTreeNode rootPathNode = new FlowTreeNode(rootPathFlow);
            baseNode.add(rootPathNode);
        } else {
            String[] array = (uri.getPath() + " ").split("/");
            int len = array.length;
            FlowTreeNode currentNode = baseNode;
            for (int i = 1; i < len; i++) {
                String fragment = "".equals(array[i].trim()) ? "/" : array[i].trim();
                if (i == (len - 1)) {
                    // Target node
                    Flow targetFlow = new Flow();
                    targetFlow.setRequestId(requestId);
                    targetFlow.setUrl(fragment);
                    targetFlow.setFlowType(FlowType.TARGET);
                    targetFlow.setIcon(icon);
                    FlowTreeNode node = new FlowTreeNode(targetFlow);
                    currentNode.add(node);
                } else {
                    final FlowTreeNode finalNode = currentNode;
                    currentNode = findFirstChild(currentNode, fragment).orElseGet(() -> {
                        // Path node
                        Flow pathFlow = new Flow();
                        pathFlow.setUrl(fragment);
                        pathFlow.setFlowType(FlowType.PATH);
                        FlowTreeNode node = new FlowTreeNode(pathFlow);
                        finalNode.add(node);
                        return node;
                    });
                }
            }
        }
    }

    public FlowTreeNode findLeaf(FlowTreeNode node, Integer requestId) {
        if (node.isLeaf()) {
            Flow flow = (Flow) node.getUserObject();
            if (null != flow && flow.getRequestId().equals(requestId)) {
                return node;
            } else {
                return null;
            }
        }
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            FlowTreeNode child = (FlowTreeNode) node.getChildAt(i);
            FlowTreeNode treeNode = findLeaf(child, requestId);
            if (null != treeNode) {
                return treeNode;
            }
        }
        return null;
    }

    public Optional<FlowTreeNode> findFirstChild(FlowTreeNode node, String url) {
        List<FlowTreeNode> children = findChildren(node, url);
        return Optional.ofNullable(children.size() == 0 ? null : children.get(0));
    }

    public List<FlowTreeNode> findChildren(FlowTreeNode node, String url) {
        List<FlowTreeNode> list = new LinkedList<>();
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            FlowTreeNode child = (FlowTreeNode) node.getChildAt(i);
            Flow flow = (Flow) child.getUserObject();
            if (null == url || flow.getUrl().equals(url)) {
                list.add(child);
            }
        }
        return list;
    }
}
