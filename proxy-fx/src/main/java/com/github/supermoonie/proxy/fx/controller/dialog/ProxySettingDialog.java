package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.proxy.intercept.InternalProxyInterceptInitializer;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/3
 */
public class ProxySettingDialog implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProxySettingDialog.class);

    @FXML
    protected VBox container;
    @FXML
    protected TextField portTextField;
    @FXML
    protected ToggleSwitch authToggleSwitch;
    @FXML
    protected TextField usernameTextField;
    @FXML
    protected TextField passwordTextField;
    @FXML
    protected Button confirmButton;
    @FXML
    protected Button cancelButton;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portTextField.setText(String.valueOf(GlobalSetting.getInstance().getPort()));
        if (GlobalSetting.getInstance().isAuth()) {
            authToggleSwitch.setSelected(true);
            usernameTextField.setDisable(false);
            passwordTextField.setDisable(false);
        } else {
            authToggleSwitch.setSelected(false);
            usernameTextField.setDisable(true);
            passwordTextField.setDisable(true);
        }
        authToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            usernameTextField.setDisable(!newValue);
            passwordTextField.setDisable(!newValue);
        });
        usernameTextField.setText(GlobalSetting.getInstance().getUsername());
        passwordTextField.setText(GlobalSetting.getInstance().getPassword());
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
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (authToggleSwitch.isSelected()) {
            if (StringUtils.isEmpty(username)) {
                AlertUtil.warning("Username Is Empty !");
                usernameTextField.requestFocus();
                return;
            }
            if (StringUtils.isEmpty(password)) {
                AlertUtil.warning("Password Is Empty !");
                passwordTextField.requestFocus();
                return;
            }
        }
        final GlobalSetting instance = GlobalSetting.getInstance();
        boolean restartFlag = instance.getPort() != port || authToggleSwitch.isSelected() != instance.isAuth() || !username.equals(instance.getUsername()) || !password.equals(instance.getPassword());
        if (restartFlag) {
            confirmButton.setText("Restarting...");
            confirmButton.setDisable(true);
            cancelButton.setDisable(true);
            App.EXECUTOR.execute(() -> {
                ProxyManager.restart(port, authToggleSwitch.isSelected(), username, password, InternalProxyInterceptInitializer.INSTANCE);
                Platform.runLater(() -> {
                    instance.setPort(port);
                    instance.setAuth(authToggleSwitch.isSelected());
                    instance.setUsername(username);
                    instance.setPassword(password);
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

    public static void show() {
        Stage proxySettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(ProxySettingDialog.class.getResource("/ui/dialog/ProxySettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            ProxySettingDialog proxySettingDialog = fxmlLoader.getController();
            proxySettingDialog.setStage(proxySettingStage);
            proxySettingStage.setScene(new Scene(parent));
            App.setCommonIcon(proxySettingStage, "Lightning");
            proxySettingStage.initModality(Modality.APPLICATION_MODAL);
            proxySettingStage.setResizable(false);
            proxySettingStage.initStyle(StageStyle.UTILITY);
            proxySettingStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }
}
