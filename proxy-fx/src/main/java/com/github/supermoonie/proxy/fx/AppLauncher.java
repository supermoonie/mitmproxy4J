package com.github.supermoonie.proxy.fx;

import javafx.application.Application;

/**
 * @author supermoonie
 * @since 2020/9/16
 */
public class AppLauncher {

    public static void main(String[] args) {
        System.setProperty("apple.awt.UIElement", "true");
        java.awt.Toolkit.getDefaultToolkit();
        Application.launch(App.class);
    }
}
