package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.controller.main.MainController;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.proxy.intercept.DefaultConfigIntercept;
import com.github.supermoonie.proxy.fx.proxy.intercept.InternalProxyInterceptInitializer;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.support.AllowUrl;
import com.github.supermoonie.proxy.fx.support.BlockUrl;
import com.github.supermoonie.proxy.fx.tray.SystemTrayManager;
import com.github.supermoonie.proxy.fx.util.SettingUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 * @author supermoonie
 */
public class App extends Application {

    private final Logger log = LoggerFactory.getLogger(App.class);

    public static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(5);

    public static void main(String[] args) {
        launch(args);
    }

    private DefaultConfigIntercept defaultConfigIntercept;

    private static Stage primaryStage;

    private static MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        App.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Main.fxml"));
        Parent root = fxmlLoader.load();
        mainController = fxmlLoader.getController();
        primaryStage.setScene(new Scene(root));
        setCommonIcon(primaryStage);
//        new InspectNode(primaryStage);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            SwingUtilities.invokeLater(SystemTrayManager::destroy);
            EXECUTOR.shutdown();
            Platform.runLater(() -> {
                SettingUtil.save(GlobalSetting.getInstance());
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @Override
    public void init() throws Exception {
        DaoCollections.init();
        Platform.runLater(() -> {
            SettingUtil.load();
            initSetting();
        });
        EXECUTOR.scheduleAtFixedRate(() -> SettingUtil.save(GlobalSetting.getInstance()), 10, 30, TimeUnit.SECONDS);
        SystemTrayManager.init();
        this.notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
    }

    private void initSetting() {
        GlobalSetting instance = GlobalSetting.getInstance();
        ProxyManager.start(instance.getPort(), instance.isAuth(), instance.getUsername(), instance.getPassword(), InternalProxyInterceptInitializer.INSTANCE);
        ProxyManager.getInternalProxy().setTrafficShaping(instance.isThrottling());
        defaultConfigIntercept.setAllowFlag(instance.isAllowUrl());
        defaultConfigIntercept.setBlockFlag(instance.isBlockUrl());
        for (AllowUrl allowUrl : instance.getAllowUrlList()) {
            if (allowUrl.isEnable()) {
                defaultConfigIntercept.getAllowUriList().add(allowUrl.getUrlRegex());
            }
        }
        for (BlockUrl blockUrl : instance.getBlockUrlList()) {
            if (blockUrl.isEnable()) {
                defaultConfigIntercept.getBlockUriList().add(blockUrl.getUrlRegex());
            }
        }
        try {
            if (instance.isSystemProxy()) {
                ProxyManager.enableSystemProxy();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        instance.portProperty().addListener((observable, oldValue, newValue) -> primaryStage.setTitle("Lighting:" + newValue));
        instance.blockUrlProperty().addListener((observable, oldValue, newValue) -> defaultConfigIntercept.setBlockFlag(newValue));
        instance.blockUrlListProperty().addListener((observable, oldValue, newValue) -> {
            defaultConfigIntercept.getBlockUriList().clear();
            for (BlockUrl blockUrl : newValue) {
                if (blockUrl.isEnable()) {
                    defaultConfigIntercept.getBlockUriList().add(blockUrl.getUrlRegex());
                }
            }
        });
        instance.allowUrlProperty().addListener((observable, oldValue, newValue) -> defaultConfigIntercept.setAllowFlag(newValue));
        instance.allowUrlListProperty().addListener((observable, oldValue, newValue) -> {
            defaultConfigIntercept.getAllowUriList().clear();
            for (AllowUrl allowUrl : newValue) {
                if (allowUrl.isEnable()) {
                    defaultConfigIntercept.getAllowUriList().add(allowUrl.getUrlRegex());
                }
            }
        });
    }


    public static void setCommonIcon(Stage stage) {
        setCommonIcon(stage, "Lightning | Listening on " + ProxyManager.getInternalProxy().getPort());
    }

    public static void setCommonIcon(Stage stage, String title) {
        URL iconUrl;
        if (PlatformUtil.isWindows()) {
            iconUrl = App.class.getResource("/lightning-win.png");
        } else {
            iconUrl = App.class.getResource("/lightning-mac.png");
        }
        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle(title);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.close();
            }
        });
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static MainController getMainController() {
        return mainController;
    }
}

