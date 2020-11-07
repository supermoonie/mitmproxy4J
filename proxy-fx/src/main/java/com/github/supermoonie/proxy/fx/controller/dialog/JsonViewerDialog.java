package com.github.supermoonie.proxy.fx.controller.dialog;

import com.github.supermoonie.proxy.fx.App;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.bouncycastle.util.encoders.Hex;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/11/6
 */
public class JsonViewerDialog implements Initializable {

    private Stage stage;

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

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
