package com.github.supermoonie.proxy.fx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
