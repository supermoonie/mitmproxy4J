package com.github.supermoonie.proxy.fx;

import com.sun.javafx.application.LauncherImpl;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.desktop.PreferencesHandler;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author supermoonie
 * @date 2021-02-21
 */
public class Mitmproxy4J {

    private static final Logger log = LoggerFactory.getLogger(Mitmproxy4J.class);

    public static void main(String[] args) {
        if (PlatformDependent.isOsx()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            try {
                Class<?> appleAppClass = Class.forName("com.apple.eawt.Application");
                Method getApplication = appleAppClass.getMethod("getApplication");
                Object app = getApplication.invoke(appleAppClass);
                Method setPreferencesHandler = appleAppClass.getMethod("setPreferencesHandler", PreferencesHandler.class);
                setPreferencesHandler.invoke(app, (PreferencesHandler) e -> System.out.println("..."));
                Method setDockIconImage = appleAppClass.getMethod("setDockIconImage", Image.class);
                URL url = App.class.getClassLoader().getResource("mitm.png");
                Image image = Toolkit.getDefaultToolkit().getImage(url);
                setDockIconImage.invoke(app, image);
//                com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
//                app.setQuitHandler((e, response) -> response.performQuit());
//                app.setAboutHandler(e -> {
//                    // TODO
//                    System.out.println("about");
//                });
//                app.setPreferencesHandler(e -> new AppearanceDialogController(MAIN_FRAME, "Preferences", true).setVisible(true));
//                URL url = Application.class.getClassLoader().getResource("M.png");
//                Image image = Toolkit.getDefaultToolkit().getImage(url);
//                app.setDockIconImage(image);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                //This means that the application is not being run on MAC OS.
                //Just do nothing and go on...
            }
        }
        LauncherImpl.launchApplication(App.class, SplashScreenLoader.class, args);
    }
}
