package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.controller.main.MainController;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.proxy.intercept.InternalProxyInterceptInitializer;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.tray.SystemTrayManager;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
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
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.prefs.Preferences;

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

    private static final String PREFS_ROOT_PATH = "/mitmproxy4j";

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
        log.info("init app preferences");
        AppPreferences.init(PREFS_ROOT_PATH);
        log.info("init db");
        DaoCollections.init();
        Thread.setDefaultUncaughtExceptionHandler(AlertUtil::showError);
        log.info("init proxy");
        initProxy();
        this.notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
    }

    private void initProxy() {
        Preferences state = AppPreferences.getState();
        int port = state.getInt(AppPreferences.KEY_PROXY_PORT, AppPreferences.DEFAULT_PROXY_PORT);
        boolean auth = state.getBoolean(AppPreferences.KEY_PROXY_AUTH, AppPreferences.DEFAULT_PROXY_AUTH);
        String username = state.get(AppPreferences.KEY_PROXY_AUTH_USER, "");
        String password = state.get(AppPreferences.KEY_PROXY_AUTH_PWD, "");
        ProxyManager.start(port, auth, username, password, InternalProxyInterceptInitializer.INSTANCE);
        long writeLimit = state.getLong(AppPreferences.KEY_PROXY_LIMIT_WRITE, AppPreferences.DEFAULT_PROXY_LIMIT_WRITE);
        long readLimit = state.getLong(AppPreferences.KEY_PROXY_LIMIT_READ, AppPreferences.DEFAULT_PROXY_LIMIT_READ);
        ProxyManager.setWriteLimit(writeLimit);
        ProxyManager.setReadLimit(readLimit);
        log.info("mitmproxy4J is listening on " + port);
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

