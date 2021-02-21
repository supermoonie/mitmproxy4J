package com.github.supermoonie.proxy.fx;

import com.sun.javafx.application.LauncherImpl;

/**
 * @author supermoonie
 * @date 2021-02-21
 */
public class Mitmproxy4JLauncher {

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Mitmproxy4J.class, SplashScreenLoader.class, args);
    }
}
