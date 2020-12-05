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
 * JTree 中的结点
 *
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

    /**
     * 更新JTree 中的叶子结点，
     * 如果响应为 null，将叶子结点图标设置为下载图标，
     * 如果不为 null，则根据响应的 status 以及 content-type 设置响应图标
     *
     * @param request  请求
     * @param response 响应
     */
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

    /**
     * 添加结点，域名作为根结点，每一级Path作为枝干，最后一级Path作为叶子结点
     *
     * @param flow  Flow
     * @throws URISyntaxException 无法识别出URL时抛出此异常
     */
    public void add(Flow flow) throws URISyntaxException {
        URI uri = new URI(flow.getUrl());
        String baseUrl = uri.getScheme() + "://" + uri.getAuthority();
        // 根据域名在跟结点的子节点中查找，有则返回，无则创建
        FlowTreeNode baseNode = findFirstChild(this, baseUrl).orElseGet(() -> {
            Flow baseFlow = new Flow();
            baseFlow.setUrl(baseUrl);
            baseFlow.setFlowType(FlowType.BASE_URL);
            FlowTreeNode node = new FlowTreeNode(baseFlow);
            this.add(node);
            return node;
        });
        // 如果没有Path，则将 "/" 设置为叶子结点
        if ("".equals(uri.getPath()) || "/".equals(uri.getPath())) {
            Flow leafFlow = new Flow();
            leafFlow.setRequestId(flow.getRequestId());
            leafFlow.setUrl("/");
            leafFlow.setFlowType(FlowType.TARGET);
            leafFlow.setIcon(flow.getIcon());
            leafFlow.setResponseId(flow.getResponseId());
            leafFlow.setContentType(flow.getContentType());
            FlowTreeNode leaf = new FlowTreeNode(leafFlow);
            baseNode.add(leaf);
        } else {
            String[] pathArr = (uri.getPath() + " ").split("/");
            int len = pathArr.length;
            FlowTreeNode currentNode = baseNode;
            // 遍历每一级Path
            for (int i = 1; i < len; i++) {
                String path = "".equals(pathArr[i].trim()) ? "/" : pathArr[i].trim();
                // 最后一级Path设置为叶子结点
                if (i == (len - 1)) {
                    Flow leafFlow = new Flow();
                    leafFlow.setRequestId(flow.getRequestId());
                    leafFlow.setUrl(path);
                    leafFlow.setFlowType(FlowType.TARGET);
                    leafFlow.setIcon(flow.getIcon());
                    leafFlow.setResponseId(flow.getResponseId());
                    leafFlow.setContentType(flow.getContentType());
                    FlowTreeNode leaf = new FlowTreeNode(leafFlow);
                    currentNode.add(leaf);
                } else {
                    final FlowTreeNode pathNode = currentNode;
                    // 当前分支查找具有相同Path的结点，有则返回，无则创建
                    currentNode = findFirstChild(currentNode, path).orElseGet(() -> {
                        Flow pathFlow = new Flow();
                        pathFlow.setUrl(path);
                        pathFlow.setFlowType(FlowType.PATH);
                        FlowTreeNode node = new FlowTreeNode(pathFlow);
                        pathNode.add(node);
                        return node;
                    });
                }
            }
        }
    }

    /**
     * 在指定结点下查找指定requestId的叶子结点
     *
     * @param node      结点
     * @param requestId 要查找的结点的requestId
     * @return 叶子结点
     */
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

    /**
     * 在指定结点下查找指定path的第一个结点
     *
     * @param node 结点
     * @param path 路径
     * @return 第一个结点
     */
    public Optional<FlowTreeNode> findFirstChild(FlowTreeNode node, String path) {
        List<FlowTreeNode> children = findChildren(node, path);
        return Optional.ofNullable(children.size() == 0 ? null : children.get(0));
    }

    /**
     * 在指定结点下查找指定path的结点集合
     *
     * @param node 结点
     * @param path 路径
     * @return 结点集合
     */
    public List<FlowTreeNode> findChildren(FlowTreeNode node, String path) {
        List<FlowTreeNode> list = new LinkedList<>();
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            FlowTreeNode child = (FlowTreeNode) node.getChildAt(i);
            Flow flow = (Flow) child.getUserObject();
            if (null == path || flow.getUrl().equals(path)) {
                list.add(child);
            }
        }
        return list;
    }
}
