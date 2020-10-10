package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.tray.SystemTrayManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.swing.*;

/**
 * Hello world!
 */
@SpringBootApplication
public class App extends Application {

    //    public static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(5);

    public static void main(String[] args) {
        launch(args);
    }

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private SystemTrayManager systemTrayManager;

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.setOnCloseRequest(windowEvent -> {
            SpringApplication.exit(applicationContext, () -> 0);
            Platform.runLater(() -> {
                SwingUtilities.invokeLater(() -> systemTrayManager.destroy());
                Platform.exit();
            });
        });
    }

    @Override
    public void init() throws Exception {
        SpringApplication.run(getClass()).getAutowireCapableBeanFactory().autowireBean(this);
        systemTrayManager.init();
        Platform.setImplicitExit(false);
        this.notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}

