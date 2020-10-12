package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.controller.MainController;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.tray.SystemTrayManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import com.github.supermoonie.proxy.fx.proxy.intercept.InternalProxyInterceptInitializer;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Hello world!
 */
@SpringBootApplication
@MapperScan(basePackages = "com.github.supermoonie.proxy.fx.mapper")
public class App extends Application {

    //    public static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(5);

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
    private JdbcTemplate jdbcTemplate;

    private static Stage primaryStage;

    private static MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        App.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Main.fxml"));
        mainController = fxmlLoader.getController();
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.setOnCloseRequest(windowEvent -> {
            SpringApplication.exit(applicationContext, () -> 0);
            Platform.runLater(() -> {
                SwingUtilities.invokeLater(() -> systemTrayManager.destroy());
                System.exit(0);
//                ProxyManager.stop();
//                Platform.exit();
            });
        });
    }

    @Override
    public void init() throws Exception {
        SpringApplication.run(getClass()).getAutowireCapableBeanFactory().autowireBean(this);
        initDatabase();
        ProxyManager.start(10801, initializer);
        systemTrayManager.init();
        this.notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
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

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static MainController getMainController() {
        return mainController;
    }
}

