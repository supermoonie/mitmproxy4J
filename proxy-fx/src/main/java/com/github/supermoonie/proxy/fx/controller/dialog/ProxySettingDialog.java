package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.github.supermoonie.proxy.fx.proxy.intercept.InternalProxyInterceptInitializer;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationContextUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.util.StringUtils;

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
                InternalProxyInterceptInitializer initializer = ApplicationContextUtil.getBean(InternalProxyInterceptInitializer.class);
                ProxyManager.restart(port, authToggleSwitch.isSelected(), username, password, initializer);
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
}
