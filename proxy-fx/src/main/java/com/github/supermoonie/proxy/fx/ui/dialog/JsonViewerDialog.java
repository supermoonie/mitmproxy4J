package com.github.supermoonie.proxy.fx.ui.dialog;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/6
 */
public class JsonViewerDialog implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(JsonViewerDialog.class);

    @FXML
    protected TextArea srcTextArea;
    @FXML
    protected WebView destWebView;
    @FXML
    protected Button formatButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        destWebView.getEngine().load(App.class.getResource("/static/RichText.html").toExternalForm());
        destWebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Worker.State.SUCCEEDED)) {
                destWebView.getEngine().executeScript(String.format("setHexJson('%s');codeEditor.setReadOnly(true);", Hex.toHexString("".getBytes(StandardCharsets.UTF_8))));
            }
        });
    }

    public void onFormatButtonClicked() {
        String text = srcTextArea.getText();
        destWebView.getEngine().executeScript(String.format("setHexJson('%s');", Hex.toHexString(text.getBytes(StandardCharsets.UTF_8))));
    }

    public static void show() {
        Stage jsonViewerStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(JsonViewerDialog.class.getResource("/ui/dialog/JsonViewerDialog.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            fxmlLoader.getController();
            jsonViewerStage.setScene(new Scene(parent));
            App.setCommonIcon(jsonViewerStage, "JSON Viewer");
            jsonViewerStage.show();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }
}
