package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.proxy.intercept.InternalProxyInterceptInitializer;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/3
 */
public class ProxySettingDialog implements Initializable {

    @FXML
    protected VBox container;

    @FXML
    protected TextField portTextField;

    @FXML
    protected Button confirmButton;

    @FXML
    protected Button cancelButton;

    private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portTextField.setText(String.valueOf(GlobalSetting.getInstance().getPort()));
    }

    public void onConfirmButtonClicked() {
        String p = portTextField.getText();
        int port;
        try {
            port = Integer.parseInt(p);
        } catch (NumberFormatException e) {
            AlertUtil.error(e);
            portTextField.requestFocus();
            return;
        }
        if (GlobalSetting.getInstance().getPort() != port) {
            confirmButton.setText("Restarting...");
            confirmButton.setDisable(true);
            cancelButton.setDisable(true);
            App.EXECUTOR.execute(() -> {
                InternalProxyInterceptInitializer initializer = ApplicationContextUtil.getBean(InternalProxyInterceptInitializer.class);
                ProxyManager.restart(port, initializer);
                Platform.runLater(() -> {
                    GlobalSetting.getInstance().setPort(port);
                    confirmButton.setText("Confirm");
                    confirmButton.setDisable(false);
                    cancelButton.setDisable(false);
                });
            });
        } else {
            stage.close();
        }
    }

    public void onCancelButtonClicked() {
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
