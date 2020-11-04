package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/3
 */
public class ThrottlingSettingDialog implements Initializable {

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
}
