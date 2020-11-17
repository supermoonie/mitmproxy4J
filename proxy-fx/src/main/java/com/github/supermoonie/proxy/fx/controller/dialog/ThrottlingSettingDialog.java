package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/3
 */
public class ThrottlingSettingDialog implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ThrottlingSettingDialog.class);

    @FXML
    protected TextField downloadTextField;
    @FXML
    protected TextField uploadTextField;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        downloadTextField.setText(decimalFormat.format(GlobalSetting.getInstance().getThrottlingReadLimit() / 1024.0));
        uploadTextField.setText(decimalFormat.format(GlobalSetting.getInstance().getThrottlingWriteLimit() / 1024.0));
    }

    public void onConfirmButtonClicked() {
        String download = downloadTextField.getText();
        long dl;
        try {
            dl = (long)(Double.parseDouble(download) * 1024);
        } catch (NumberFormatException e) {
            AlertUtil.error(e);
            downloadTextField.requestFocus();
            return;
        }
        String upload = uploadTextField.getText();
        long ul;
        try {
            ul = (long) (Double.parseDouble(upload) * 1024);
        } catch (NumberFormatException e) {
            AlertUtil.error(e);
            uploadTextField.requestFocus();
            return;
        }
        GlobalSetting.getInstance().setThrottlingReadLimit(dl);
        GlobalSetting.getInstance().setThrottlingWriteLimit(ul);
        stage.close();
    }

    public void onCancelButtonClicked() {
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void show() {
        Stage throttlingSettingStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(ThrottlingSettingDialog.class.getResource("/ui/dialog/ThrottlingSettingDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            ThrottlingSettingDialog throttlingSettingDialog = fxmlLoader.getController();
            throttlingSettingDialog.setStage(throttlingSettingStage);
            throttlingSettingStage.setScene(new Scene(parent));
            App.setCommonIcon(throttlingSettingStage, "Throttling Setting");
            throttlingSettingStage.initModality(Modality.APPLICATION_MODAL);
            throttlingSettingStage.setResizable(false);
            throttlingSettingStage.initStyle(StageStyle.UTILITY);
            throttlingSettingStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }
}
