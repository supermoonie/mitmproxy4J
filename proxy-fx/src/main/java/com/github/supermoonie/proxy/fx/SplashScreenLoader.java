package com.github.supermoonie.proxy.fx;

import javafx.application.Preloader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author supermoonie
 * @since 2020/10/9
 */
public class SplashScreenLoader extends Preloader {

    private Stage splashScreen;

    @Override
    public void start(Stage stage) throws Exception {
        splashScreen = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        Scene splashScene = new Scene(getParent(), Color.TRANSPARENT);
        stage.setScene(splashScene);
        stage.show();
        stage.toFront();
    }

    public Parent getParent() {
        final ImageView imageView = new ImageView(getClass().getResource("/splash/javafx.png").toExternalForm());
        final ProgressBar splashProgressBar = new ProgressBar();
        splashProgressBar.setPrefWidth(imageView.getImage().getWidth());
        final VBox vbox = new VBox();
        vbox.getChildren().addAll(imageView, splashProgressBar);
        return vbox;
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        if (notification instanceof StateChangeNotification) {
            splashScreen.hide();
        }
    }
}
