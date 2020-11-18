package com.github.supermoonie.proxy.fx.swing;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/11/18
 */
public class JTreeDemo {

    public static void main(String[] args)
    {
        JFrame frame=new JFrame("教师学历信息");
        frame.setSize(330,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JTreeDemo().createTreeTable());
        frame.pack();
        frame.setVisible(true);
    }


    private JScrollPane createTreeTable() {
        JScrollPane scrollPane = new JScrollPane();
//        JXTreeTable treeTable = new JXTreeTable();

        JTreeTable treeTable = new JTreeTable(new AbstractTreeTableModel("root") {
            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public String getColumnName(int column) {
                return null;
            }

            @Override
            public Object getValueAt(Object node, int column) {
                return null;
            }

            @Override
            public Object getChild(Object parent, int index) {
                return null;
            }

            @Override
            public int getChildCount(Object parent) {
                return 0;
            }
        });
//        treeTable.setRootVisible(true);
        scrollPane.setViewportView(treeTable);
        scrollPane.setVisible(true);
        return scrollPane;
    }

    private JScrollPane createComponent()
    {
        JScrollPane scrollPane = new JScrollPane();

        DefaultMutableTreeNode root=new DefaultMutableTreeNode("教师学历信息");
        String Teachers[][]=new String[3][];
        Teachers[0]=new String[]{"王鹏","李曼","韩小国","穆保龄","尚凌云","范超峰"};
        Teachers[1]=new String[]{"胡会强","张春辉","宋芳","阳芳","朱山根","张茜","宋媛媛"};
        Teachers[2]=new String[]{"刘丹","张小芳","刘华亮","聂来","吴琼"};
        String gradeNames[]={"硕士学历","博士学历","博士后学历"};
        DefaultMutableTreeNode node=null;
        DefaultMutableTreeNode childNode=null;
        int length=0;
        for(int i=0;i<3;i++)
        {
            length=Teachers[i].length;
            node=new DefaultMutableTreeNode(gradeNames[i]);
            for (int j=0;j<length;j++)
            {
                childNode=new DefaultMutableTreeNode(Teachers[i][j]);
                node.add(childNode);
            }
            root.add(node);
        }
        JTree tree=new JTree(root);
        scrollPane.setViewportView(tree);
        scrollPane.setVisible(true);
        return scrollPane;
    }
}
