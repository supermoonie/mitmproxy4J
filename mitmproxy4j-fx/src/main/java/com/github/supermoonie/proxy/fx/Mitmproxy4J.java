package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.controller.MainController;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;

/**
 * Hello world!
 *
 * @author wangc
 */
public class Mitmproxy4J extends Application {

    private static Stage primaryStage;

    private static MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Mitmproxy4J.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Main.fxml"));
        Parent root = fxmlLoader.load();
        mainController = fxmlLoader.getController();
        primaryStage.setScene(new Scene(root));
        setCommonIcon(primaryStage);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> Platform.runLater(Platform::exit));
    }

    @Override
    public void init() throws Exception {
        this.notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
    }

    public static void setCommonIcon(Stage stage) {
        setCommonIcon(stage, "Lightning | Listening on ");
    }

    public static void setCommonIcon(Stage stage, String title) {
        URL iconUrl;
        if (PlatformUtil.isWindows()) {
            iconUrl = Mitmproxy4J.class.getResource("/lightning-win.png");
        } else {
            iconUrl = Mitmproxy4J.class.getResource("/lightning-mac.png");
        }
        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle(title);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.close();
            }
        });
    }
}
