package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.flow.FlowList;
import com.github.supermoonie.proxy.swing.gui.flow.FlowTreeNode;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.j256.ormlite.dao.Dao;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FilterKeyListener extends KeyAdapter {

    public static Predicate<Flow> filter;

    private final JTextField filterField;

    public FilterKeyListener(JTextField filterField) {
        this.filterField = filterField;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String text = filterField.getText();
            filter = flow -> flow.getUrl().contains(text);
            try {
                FlowList flowList = Application.MAIN_FRAME.getFlowList();
                flowList.filter(filter);
                FlowTreeNode rootNode = Application.MAIN_FRAME.getRootNode();
                rootNode.removeAllChildren();
                flowList.clear();
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                List<Request> requestList = requestDao.queryBuilder()
                        .orderBy(Request.TIME_CREATED_FIELD_NAME, true)
                        .where()
                        .gt(Request.TIME_CREATED_FIELD_NAME, Application.START_TIME)
                        .and()
                        .like(Request.URI_FIELD_NAME, "%" + text + "%")
                        .query();
                Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
                for (Request req : requestList) {
                    Response response = responseDao.queryBuilder()
                            .where()
                            .eq(Response.REQUEST_ID_FIELD_NAME, req.getId())
                            .queryForFirst();
                    if (null != response) {
                        Flow flow = new Flow();
                        flow.setRequestId(req.getId());
                        flow.setUrl(req.getUri());
                        flow.setRequestTime(req.getTimeCreated());
                        flow.setStatus(response.getStatus());
                        flow.setContentType(response.getContentType());
                        flow.setResponseId(response.getId());
                        flow.setIcon(SvgIcons.loadIcon(response.getStatus(), response.getContentType()));
                        rootNode.add(flow);
                        flowList.add(flow);
                    }
                }
                flowList.updateUI();
                setTreeExpandedState(Application.MAIN_FRAME.getFlowTree(), true);
                Application.MAIN_FRAME.getFlowTree().updateUI();
            } catch (SQLException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setTreeExpandedState(JTree tree, boolean expanded) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
        setNodeExpandedState(tree, node, expanded);
    }

    public static void setNodeExpandedState(JTree tree, DefaultMutableTreeNode node, boolean expanded) {
        ArrayList<TreeNode> list = Collections.list(node.children());
        for (TreeNode treeNode : list) {
            setNodeExpandedState(tree, (DefaultMutableTreeNode) treeNode, expanded);
        }
        if (!expanded && node.isRoot()) {
            return;
        }
        TreePath path = new TreePath(node.getPath());
        if (expanded) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }
}
