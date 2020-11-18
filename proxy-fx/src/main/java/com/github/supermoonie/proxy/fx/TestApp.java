package com.github.supermoonie.proxy.fx;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author supermoonie
 * @since 2020/11/17
 */
public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        final SwingNode swingNode = new SwingNode();
        swingNode.prefWidth(800);
        swingNode.prefHeight(600);
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(createTreeComponent());
        });
        VBox vBox = new VBox();
        vBox.getChildren().add(swingNode);
//        CodeArea codeArea = new CodeArea();
//        codeArea.setPrefWidth(800);
//        codeArea.setPrefHeight(600);
//        String s = FileUtils.readFileToString(new File("/Users/supermoonie/Desktop/index.js"), StandardCharsets.UTF_8);
//        codeArea.appendText(s);
//        vBox.getChildren().add(codeArea);
//        vBox.getChildren().add(swingNode);
//        vBox.setPrefWidth(800);
//        vBox.setPrefHeight(600);
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }



    private JScrollPane createTreeComponent()
    {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setSize(800, 600);
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
