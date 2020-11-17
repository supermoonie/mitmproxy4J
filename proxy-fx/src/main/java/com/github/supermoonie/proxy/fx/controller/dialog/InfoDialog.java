package com.github.supermoonie.proxy.fx.controller.dialog;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/17
 */
public class InfoDialog implements Initializable {

    private Stage stage;

    public TextArea textArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
