package com.github.supermoonie.proxy.fx.util;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class AlertUtil {

    private static final Logger log = LoggerFactory.getLogger(AlertUtil.class);

    private AlertUtil() {

    }

    public static void warning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText("");
        AlertUtil.toFront(alert);
    }

    public static void showError(Thread thread, Throwable cause) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + cause.getMessage(), ButtonType.OK);
        alert.setHeaderText("Thread : " + thread.getName());
        toFront(alert);
    }

    public static void error(Throwable cause) {
        log.error(cause.getMessage(), cause);
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + cause.getMessage(), ButtonType.OK);
        alert.setHeaderText("");
        toFront(alert);
    }

    public static void info(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText("");
        AlertUtil.toFront(alert);
    }

    public static void toFront(Alert alert) {
        DialogPane root = alert.getDialogPane();
        Stage dialogStage = new Stage(StageStyle.UTILITY);
        for (ButtonType buttonType : root.getButtonTypes()) {
            ButtonBase button = (ButtonBase) root.lookupButton(buttonType);
            button.setOnAction(evt -> {
                root.setUserData(buttonType);
                dialogStage.close();
            });
        }
        // replace old scene root with placeholder to allow using root in other Scene
        root.getScene().setRoot(new Group());
        root.setPadding(new Insets(10, 0, 10, 0));
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setAlwaysOnTop(true);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
        root.getUserData();
    }
}
