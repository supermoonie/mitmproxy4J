package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.controller.MainController;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
@SpringBootApplication
@MapperScan(basePackages = "com.github.supermoonie.proxy.fx.mapper")
public class App extends Application {

    public static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(5);

    public static void main(String[] args) {
        launch(args);
    }

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private SystemTrayManager systemTrayManager;

    @Resource
    private InternalProxyInterceptInitializer initializer;

    @Resource
    private DefaultConfigIntercept defaultConfigIntercept;

    @Resource
    private JdbcTemplate jdbcTemplate;

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
            SpringApplication.exit(applicationContext, () -> 0);
            SwingUtilities.invokeLater(() -> systemTrayManager.destroy());
            EXECUTOR.shutdown();
            Platform.runLater(() -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @Override
    public void init() throws Exception {
        SpringApplication.run(getClass()).getAutowireCapableBeanFactory().autowireBean(this);
        initDatabase();
        Platform.runLater(SettingUtil::load);
        EXECUTOR.scheduleAtFixedRate(() -> SettingUtil.save(GlobalSetting.getInstance()), 10, 30, TimeUnit.SECONDS);
        ProxyManager.start(GlobalSetting.getInstance().getPort(), initializer);
        ProxyManager.getInternalProxy().setTrafficShaping(GlobalSetting.getInstance().isThrottling());
        initSetting();
        systemTrayManager.init();
        this.notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
    }

    private void initSetting() {
        GlobalSetting.getInstance().portProperty().addListener((observable, oldValue, newValue) -> primaryStage.setTitle("Lighting:" + newValue));
        GlobalSetting.getInstance().blockUrlProperty().addListener((observable, oldValue, newValue) -> defaultConfigIntercept.setBlockList(newValue));
        GlobalSetting.getInstance().blockUrlListProperty().addListener((ListChangeListener<BlockUrl>) c -> {
            ObservableList<BlockUrl> blockUrlList = GlobalSetting.getInstance().getBlockUrlList();
            for (BlockUrl blockUrl : blockUrlList) {
                if (blockUrl.isEnable()) {
                    defaultConfigIntercept.getBlockUriList().add(blockUrl.getUrlRegex());
                }
            }
        });
        GlobalSetting.getInstance().allowUrlProperty().addListener((observable, oldValue, newValue) -> defaultConfigIntercept.setAllowFlag(newValue));
        GlobalSetting.getInstance().allowUrlListProperty().addListener((ListChangeListener<AllowUrl>) c -> {
            ObservableList<AllowUrl> blockUrlList = GlobalSetting.getInstance().getAllowUrlList();
            for (AllowUrl allowUrl : blockUrlList) {
                if (allowUrl.isEnable()) {
                    defaultConfigIntercept.getBlockUriList().add(allowUrl.getUrlRegex());
                }
            }
        });
    }


    private void initDatabase() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/crate_table.sql")) {
            byte[] bytes = IOUtils.readFully(in, in.available());
            String initSql = new String(bytes);
            String[] sqlArr = initSql.split("--EOF--");
            for (String sql : sqlArr) {
                jdbcTemplate.execute(sql);
            }
        }
    }

    public static void setCommonIcon(Stage stage) {
        setCommonIcon(stage, "Lightning:" + ProxyManager.getInternalProxy().getPort());
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
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static MainController getMainController() {
        return mainController;
    }
}

