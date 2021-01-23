package com.github.supermoonie.proxy.fx;

import com.sun.javafx.application.LauncherImpl;

import java.awt.*;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author supermoonie
 * @since 2020/9/16
 */
public class AppLauncher {

    public static void main(String[] args) {
        try {
            Class<?> app = Class.forName("com.apple.eawt.Application");
            Method getApplication = app.getMethod("getApplication");
            Object application = getApplication.invoke(app);
            Class<?>[] params = new Class[1];
            params[0] = Image.class;
            Method setDockIconImage = app.getMethod("setDockIconImage", params);
            URL url = App.class.getClassLoader().getResource("lightning-mac.png");
            Image image = Toolkit.getDefaultToolkit().getImage(url);
            setDockIconImage.invoke(application, image);
        } catch (Exception ignore) {
            // Won't work on Windows or Linux.
        }
        System.setProperty("apple.awt.UIElement", "true");
        java.awt.Toolkit.getDefaultToolkit();
        LauncherImpl.launchApplication(App.class, SplashScreenLoader.class, args);
//        Application.launch(TestApp.class, args);
    }
}
